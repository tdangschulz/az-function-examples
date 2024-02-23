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
                "xxx")
                .sender().queueName("test-queue")
                // .proxyOptions(new ProxyOptions(ProxyAuthenticationType.NONE,
                // new Proxy(Type.HTTP, new InetSocketAddress("127.0.0.1", 9000)), null, null))
                .buildClient();

        List<ServiceBusMessage> messages = Arrays.asList(
                new ServiceBusMessage("test-1"),
                new ServiceBusMessage("test-2"));

        sender.sendMessages(messages);
        sender.close();

    }
}