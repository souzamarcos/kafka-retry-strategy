package souzamarcos.demo.kafka.utils;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class MessageUtils {

    public static final String HEADER_RETRY_TIMES = "x-retry-times";

    public static LocalDateTime getReceivedTimestamp(Message<TransactionMessage> message) {
        var retryCreatedAtInMillis = (Long) message.getHeaders().get(KafkaHeaders.RECEIVED_TIMESTAMP);
        assert retryCreatedAtInMillis != null;
        return Instant.ofEpochMilli(retryCreatedAtInMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Message<TransactionMessage> generateRetriedMessage(Message<TransactionMessage> message) {
        var oldRetriesValue = (Integer) message.getHeaders().get(HEADER_RETRY_TIMES);
        var newRetriesValue = Optional.ofNullable(oldRetriesValue).orElse(0) + 1;
        return MessageBuilder.withPayload(message.getPayload())
                .copyHeaders(message.getHeaders())
                .setHeader(HEADER_RETRY_TIMES, newRetriesValue)
                .build();
    }
}
