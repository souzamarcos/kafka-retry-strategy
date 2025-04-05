package souzamarcos.demo.kafka.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binding.BindingsLifecycleController;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import souzamarcos.demo.core.exception.PauseBindingException;
import souzamarcos.demo.core.exception.ReachedMaxRetriesException;
import souzamarcos.demo.kafka.common.Bindings;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static souzamarcos.demo.kafka.common.Bindings.RETRY_IN;
import static souzamarcos.demo.kafka.utils.MessageUtils.*;

@Slf4j
@AllArgsConstructor
@Component
@Configuration
public class RetryConsumer implements Consumer<Message<TransactionMessage>> {

    public static final Integer DELAY_TIME_IN_SECONDS = 10;
    public static final Integer MAX_RETRIES = 5;

    private BindingsLifecycleController bindingsLifecycleController;
    private StreamBridge streamBridge;

    @Override
    public void accept(Message<TransactionMessage> message) {

        log.info("Received message: {}", message);
        if (shouldPauseBinding(message)) {
            pauseBinding();
            throw new PauseBindingException("Pausing binding");
        }

        if (getRetries(message) >= MAX_RETRIES) {
            log.warn("Max retries reached for message: {}", message);
            throw new ReachedMaxRetriesException("Max retries reached");
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
        log.warn("Pausing binding: {}", RETRY_IN.getBindingName());
        bindingsLifecycleController.changeState(RETRY_IN.getBindingName(), BindingsLifecycleController.State.STOPPED);
        Bindings.updateLastPausedDateTime(RETRY_IN, LocalDateTime.now());
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
