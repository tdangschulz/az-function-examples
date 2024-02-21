package de.tuan.bus.queue;

import java.util.Arrays;
import java.util.List;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;

/**
 * https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/servicebus/azure-messaging-servicebus/README.md#send-messages
 */
public class Main {
    public static void main(String[] args) {

        // 'fullyQualifiedNamespace' will look similar to
        // "{your-namespace}.servicebus.windows.net"
        var sender = new ServiceBusClientBuilder().connectionString(
                "Endpoint=sb://tuan-the-busdriver.servicebus.windows.net/;SharedAccessKeyName=test-cred;SharedAccessKey=IANAPUtVyjpR2ceh4IYA7B6G9yudZM8D0+ASbOqHMeE=;EntityPath=test-queue")
                .sender().queueName("test-queue")
                .buildClient();

        List<ServiceBusMessage> messages = Arrays.asList(
                new ServiceBusMessage("test-1"),
                new ServiceBusMessage("test-2"));

        sender.sendMessages(messages);
        sender.close();

    }
}