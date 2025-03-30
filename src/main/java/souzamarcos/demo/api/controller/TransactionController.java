package souzamarcos.demo.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.*;
import souzamarcos.demo.api.controller.dto.TransactionRequestDTO;
import souzamarcos.demo.kafka.common.Bindings;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Transaction API")
public class TransactionController {

    @Autowired
    private StreamBridge streamBridge;

     @PostMapping("/")
     @Operation(summary = "Generate transactions messages", description = "Generate a number of transaction messages")
     public Object sendMessage(@RequestBody TransactionRequestDTO request) {
         for (int i = 0; i < request.numberOfMessages(); i++) {

             var transaction = new TransactionMessage(UUID.randomUUID().toString(), "Transaction " + i, (double) new Random().nextInt(1,3));
             streamBridge.send(Bindings.TRANSACTION_OUTPUT.getBindingName(), transaction);
         }

         return Map.of("count", request.numberOfMessages());
     }
}
