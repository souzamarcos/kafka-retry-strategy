package souzamarcos.demo.kafka.dto;

import java.time.LocalDateTime;

public record OrderMessage(String id, Double value, LocalDateTime dateTime) {
    public OrderMessage(String id, Double value) {
        this(id, value, LocalDateTime.now());
    }
}
