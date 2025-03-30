package souzamarcos.demo.kafka.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binding.BindingsLifecycleController;
import org.springframework.cloud.stream.endpoint.BindingsEndpoint;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import souzamarcos.demo.kafka.common.Bindings;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static souzamarcos.demo.kafka.common.Bindings.TRANSACTION_RETRY_IN;

@Slf4j
@AllArgsConstructor
@Component
public class TransactionRetryConsumer implements Consumer<Message<TransactionMessage>> {

    public static final Integer DELAY_TIME_IN_MINUTES = 1;
    private BindingsLifecycleController bindingsLifecycleController;

    @Override
    public void accept(Message<TransactionMessage> message) {


        log.warn("Retrying message : {}", message);


        if (shouldPauseBinding(message)) {
            pauseBinding();
            throw new RuntimeException("Pausing binding");
        }
    }


    private Boolean shouldPauseBinding(Message<TransactionMessage> message) {
        return message.getPayload()
                .dateTime()
                .plusMinutes(DELAY_TIME_IN_MINUTES)
                .isAfter(LocalDateTime.now());
    }

    private void pauseBinding() {

        bindingsLifecycleController.changeState(TRANSACTION_RETRY_IN.getBindingName(), BindingsLifecycleController.State.PAUSED);
        Bindings.updateLastPausedDateTime(TRANSACTION_RETRY_IN, LocalDateTime.now());
        log.warn("Pausing binding: {}", TRANSACTION_RETRY_IN.getBindingName());
    }
}
