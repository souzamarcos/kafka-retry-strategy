package souzamarcos.demo.kafka.consumers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import souzamarcos.demo.kafka.common.Bindings;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.util.function.Consumer;


@Slf4j
@AllArgsConstructor
@Component
public class TransactionConsumer implements Consumer<Message<TransactionMessage>> {

    private StreamBridge streamBridge;

    @Override
    public void accept(Message<TransactionMessage> message) {

        if (shouldRetry(message)) {
            sendToRetry(message);
            log.warn("Send to retry message: {}", message);
            return;
        }
        log.warn("Processed message: {}", message);
    }


    private boolean shouldRetry(Message<TransactionMessage> message) {
        return message.getPayload().value() >= 0;
    }


    private void sendToRetry(Message<TransactionMessage> message) {
        streamBridge.send(Bindings.TRANSACTION_RETRY_OUTPUT.getBindingName(), message);
    }
}
