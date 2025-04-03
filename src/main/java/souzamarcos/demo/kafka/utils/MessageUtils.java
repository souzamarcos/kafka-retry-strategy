package souzamarcos.demo.kafka.utils;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class MessageUtils {

    public static final String HEADER_RETRY_TIMES = "x-retry-times";
    public static final String HEADER_RETRY_ORIGIN_TOPIC = "x-retry-origin-topic";
//
    public static LocalDateTime getReceivedTimestamp(Message<TransactionMessage> message) {
        var retryCreatedAtInMillis = (Long) message.getHeaders().get(KafkaHeaders.RECEIVED_TIMESTAMP);
        assert retryCreatedAtInMillis != null;
        return Instant.ofEpochMilli(retryCreatedAtInMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String getOriginTopic(Message<TransactionMessage> message) {
        var originTopic = message.getHeaders().get(HEADER_RETRY_ORIGIN_TOPIC);
        return (String) Optional.ofNullable(originTopic).orElseThrow(() -> new IllegalArgumentException("Origin topic not found"));
    }

    public static Message<TransactionMessage> generateRetriedMessage(Message<TransactionMessage> message) {
        var oldRetriesValue = (Integer) message.getHeaders().get(HEADER_RETRY_TIMES);
        var newRetriesValue = Optional.ofNullable(oldRetriesValue).orElse(0) + 1;
        return MessageBuilder.withPayload(message.getPayload())
                .copyHeaders(message.getHeaders())
                .setHeader(HEADER_RETRY_TIMES, newRetriesValue)
                .build();
    }

    public static Message<TransactionMessage> generateRetryMessage(Message<TransactionMessage> message) {
        var originTopic =  message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC);
        return MessageBuilder.withPayload(message.getPayload())
                .copyHeaders(message.getHeaders())
                .setHeader(HEADER_RETRY_ORIGIN_TOPIC, originTopic)
                .build();
    }
}
