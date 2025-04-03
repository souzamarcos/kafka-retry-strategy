package souzamarcos.demo.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import souzamarcos.demo.api.controller.dto.TransactionRequestDTO;
import souzamarcos.demo.kafka.common.Bindings;
import souzamarcos.demo.kafka.dto.OrderMessage;
import souzamarcos.demo.kafka.dto.TransactionMessage;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@Tag(name = "Order API")
public class OrderController {

    @Autowired
    private StreamBridge streamBridge;

     @PostMapping("/")
     @Operation(summary = "Generate messages", description = "Generate a number of messages")
     public Object sendMessage(@RequestBody TransactionRequestDTO request) {
         for (int i = 0; i < request.numberOfMessages(); i++) {

             var transaction = new OrderMessage(UUID.randomUUID().toString(), (double) new Random().nextInt(1,3));
             streamBridge.send(Bindings.ORDER_OUTPUT.getBindingName(), transaction);
         }

         return Map.of("count", request.numberOfMessages());
     }
}
