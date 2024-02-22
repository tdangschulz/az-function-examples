package de.tuan.bus.topic;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

/**
 * https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/servicebus/azure-messaging-servicebus/README.md#send-messages
 */
public class Main {
    private static final String CONNECTION_STRING = "Endpoint=sb://bus-tuan-test.servicebus.windows.net/;SharedAccessKeyName=policy;SharedAccessKey=mHOZime/GYoVbMUqWT4RFsfaQYoRqsIUc+ASbJ1IW8g=;EntityPath=myfirsttopic";
    private static final String TOPIC_NAME = "myfirsttopic";
    private static final String SUBSCRIPTION_NAME = "mein-test-subscription";

    public static void main(String[] args) {
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .connectionString(CONNECTION_STRING)
                .processor()
                .subscriptionName(SUBSCRIPTION_NAME)
                .topicName(TOPIC_NAME)
                .processMessage(Main::processMessage)
                .processError(context -> processError(context))
                .buildProcessorClient();

        System.out.println("Warte auf Nachrichten...");
        processorClient.start();

        waitForEnterKeyPress();

        System.out.println("Beendet den Processor...");
        processorClient.close();
    }

    private static void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        System.out.printf("Empfangene Nachricht: '%s'%n", message.getBody());
        // context.deadLetter(new
        // DeadLetterOptions().setDeadLetterErrorDescription("Alles doof"));

        // Weitere Verarbeitung hier
    }

    private static void processError(ServiceBusErrorContext exception) {
        System.out.printf("Fehler beim Empfangen von Nachrichten: %s%n", exception.getException().getMessage());
        // Fehlerbehandlung hier
    }

    private static void waitForEnterKeyPress() {
        System.out.println("Drücken Sie 'Enter', um den Processor zu beenden.");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}