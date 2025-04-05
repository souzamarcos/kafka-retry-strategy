package souzamarcos.demo.kafka.utils;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

public class MessageUtils {

    public static final String HEADER_RETRY_TIMES = "x-retry-times";
    public static final String HEADER_RETRY_ORIGIN_TOPIC = "x-original-topic";

    public static LocalDateTime getReceivedTimestamp(Message<TransactionMessage> message) {
        var retryCreatedAtInMillis = (Long) message.getHeaders().get(KafkaHeaders.RECEIVED_TIMESTAMP);
        assert retryCreatedAtInMillis != null;
        return Instant.ofEpochMilli(retryCreatedAtInMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static int getRetries(Message<TransactionMessage> message) {
        var retries = message.getHeaders().get(HEADER_RETRY_TIMES);
        return (int) Optional.ofNullable(retries).orElse(1);
    }

    public static String getOriginTopic(Message<TransactionMessage> message) {
        return new String((byte[]) Objects.requireNonNull(message.getHeaders().get(HEADER_RETRY_ORIGIN_TOPIC)));
    }

    public static Message<TransactionMessage> generateRetriedMessage(Message<TransactionMessage> message) {
        var newRetriesValue = getRetries(message) + 1;
        return MessageBuilder.withPayload(message.getPayload())
                .copyHeaders(message.getHeaders())
                .setHeader(HEADER_RETRY_TIMES, newRetriesValue)
                .build();
    }

    public static Message<TransactionMessage> generateRetryMessage(Message<TransactionMessage> message) {
        return MessageBuilder.withPayload(message.getPayload())
                .copyHeaders(message.getHeaders())
                .build();
    }
}
