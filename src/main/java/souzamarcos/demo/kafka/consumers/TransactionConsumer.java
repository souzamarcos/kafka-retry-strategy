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

    @Override
    public void accept(Message<TransactionMessage> message) {

        throw new RuntimeException("Unexpected error");

//        log.warn("Processed message: {}", message);
    }

}
