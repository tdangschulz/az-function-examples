package de.tuan.order.order.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.storage.queue.QueueClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.tuan.order.order.model.Order;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private QueueClient queueClient;

    @Autowired()
    private ServiceBusSenderClient serviceBusSenderClient;

    @PostMapping("order")
    public String acceptOrder(@RequestBody Order order) throws Exception {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            var json = objectMapper.writeValueAsString(order);

            String encodedString = Base64.getEncoder().encodeToString(json.getBytes());
            this.queueClient.sendMessage(encodedString);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return "okay";

    }

    @DeleteMapping("order/{orderNr}")
    public void cancelOrder(@PathVariable String orderNr) {

        var message = new ServiceBusMessage(orderNr);
        message.getApplicationProperties().put("state", "cancel");

        this.serviceBusSenderClient.sendMessage(message);
    }
}
