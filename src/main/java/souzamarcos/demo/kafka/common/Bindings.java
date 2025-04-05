package souzamarcos.demo.kafka.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Bindings {
    TRANSACTION_OUTPUT("transactionConsumer-out-0"),
    ORDER_OUTPUT("orderConsumer-out-0"),
    RETRY_IN("retryConsumer-in-0"),
    RETRY_OUTPUT("retryConsumer-out-0");

    private final String bindingName;

    private static final Map<Bindings, LocalDateTime> lastPausedDateTime = new HashMap<>();

    public static LocalDateTime getLastPausedDateTime(Bindings binding) {
        return lastPausedDateTime.get(binding);
    }
    public static void updateLastPausedDateTime(Bindings binding, LocalDateTime dateTime) {
        lastPausedDateTime.put(binding, dateTime);
    }
}
