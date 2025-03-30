package souzamarcos.demo.kafka.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionMessage(String id, String name, Double value, LocalDateTime dateTime) {
    public TransactionMessage(String id, String name, Double value) {
        this(id, name, value, LocalDateTime.now());
    }
}
