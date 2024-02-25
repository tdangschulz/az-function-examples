package de.tuan.order.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;

@Configuration
public class AzureClientFactory {

    private final static String queueName = "order-accepted-queue";

    private String queueEndpoint = "xxx";
    private final static String topicEndpoint = "xxx";

    @Bean
    public QueueClient createQueueClient() {
        QueueClient queueClient = new QueueClientBuilder()

                .connectionString(queueEndpoint)
                .queueName(queueName)
                .buildClient();

        queueClient.createIfNotExists();

        return queueClient;
    }

    @Bean
    public ServiceBusSenderClient createServiceBusSenderClient() {
        var sender = new ServiceBusClientBuilder()
                // .proxyOptions(new ProxyOptions(ProxyAuthenticationType.NONE,
                // new Proxy(Type.HTTP, new InetSocketAddress("127.0.0.1", 9000)), null, null))
                .connectionString(topicEndpoint)
                .sender()
                .buildClient();
        return sender;
    }
}
