package de.tuan;

import java.util.function.Consumer;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusException;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;

/**
 * https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/servicebus/azure-messaging-servicebus/README.md#receive-messages
 */
public class Main {

    public static void main(String[] args) {

        Consumer<ServiceBusReceivedMessageContext> processMessage = context -> {
            final ServiceBusReceivedMessage message = context.getMessage();
            System.out.printf("Processing message. Session: %s, Sequence #: %s. Contents: %s%n",
                    message.getSessionId(), message.getSequenceNumber(), message.getBody());
        };

        Consumer<ServiceBusErrorContext> processError = errorContext -> {
            if (errorContext.getException() instanceof ServiceBusException) {
                ServiceBusException exception = (ServiceBusException) errorContext.getException();

                System.out.printf("Error source: %s, reason %s%n", errorContext.getErrorSource(),
                        exception.getReason());
            } else {
                System.out.printf("Error occurred: %s%n", errorContext.getException());
            }
        };

        var receiver = new ServiceBusClientBuilder().connectionString(
                "x")
                .processor()
                .queueName("test-queue")
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .processMessage(processMessage)
                .processError(processError)
                .buildProcessorClient();

        receiver.start();

    }
}