package souzamarcos.demo.kafka.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binding.BindingsLifecycleController;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import souzamarcos.demo.kafka.common.Bindings;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static souzamarcos.demo.kafka.common.Bindings.TRANSACTION_RETRY_IN;
import static souzamarcos.demo.kafka.utils.MessageUtils.*;

@Slf4j
@AllArgsConstructor
@Component
@Configuration
public class RetryConsumer implements Consumer<Message<TransactionMessage>> {

    public static final Integer DELAY_TIME_IN_SECONDS = 10;
    private BindingsLifecycleController bindingsLifecycleController;
    private StreamBridge streamBridge;

    @Override
    public void accept(Message<TransactionMessage> message) {

        log.info("Received message: {}", message);
        if (shouldPauseBinding(message)) {
            pauseBinding();

            throw new RuntimeException("Pausing binding");
        }


        sendToOriginTopic(message);

    }


    private Boolean shouldPauseBinding(Message<TransactionMessage> message) {
        var retryCreatedAt = getReceivedTimestamp(message);

        return retryCreatedAt
                .plusSeconds(DELAY_TIME_IN_SECONDS)
                .isAfter(LocalDateTime.now());
    }

    private void pauseBinding() {
        log.warn("Pausing binding: {}", TRANSACTION_RETRY_IN.getBindingName());
        bindingsLifecycleController.changeState(TRANSACTION_RETRY_IN.getBindingName(), BindingsLifecycleController.State.STOPPED);
        Bindings.updateLastPausedDateTime(TRANSACTION_RETRY_IN, LocalDateTime.now());
    }

    private void sendToOriginTopic(Message<TransactionMessage> message) {
        var originTopic = getOriginTopic(message);
        var newMessage = generateRetriedMessage(message);
        log.warn("Retrying message : {}", message);
        var sent = streamBridge.send(originTopic, newMessage);
        if (!sent) {
            throw new RuntimeException("Did not send to origin topic");
        }
    }


}
