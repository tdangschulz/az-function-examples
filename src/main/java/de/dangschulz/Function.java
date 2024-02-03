package de.dangschulz;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

/**
 * Azure Functions with HTTP Trigger.
 * https://learn.microsoft.com/de-de/azure/azure-functions/functions-bindings-storage-queue-trigger?tabs=python-v2%2Cisolated-process%2Cnodejs-v4%2Cextensionv5&pivots=programming-language-java
 * https://learn.microsoft.com/en-us/azure/communication-services/quickstarts/email/send-email?tabs=linux%2Cconnection-string&pivots=programming-language-java
 */
public class Function {

    /**
     * 
     * Diese Funktion wird ausgeführt, wenn eine neue Nachricht in der Azure Queue
     * erscheint.
     */
    @FunctionName("SendMailOnNewQueueMessage")
    public void run(
            @QueueTrigger(name = "message", queueName = "queue-new-account", connection = "AzureWebJobsStorage") String message,
            final ExecutionContext context) {
        context.getLogger().info("Java Queue trigger function processed a message: " + message);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Account person = mapper.readValue(message, Account.class);
            System.out.println(person.getName());

            String connectionString = "endpoint=https://<resource-name>.communication.azure.com/;accesskey=<access-key>";

            EmailClient emailClient = new EmailClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            EmailMessage emailMessage = new EmailMessage()
                    .setSenderAddress("<donotreply@974a5bc6-ddc0-49b4-96e4-f81afb49f97a.azurecomm.net>")
                    .setToRecipients(person.getEmail())
                    .setSubject("Welcome " + person.getName())
                    .setBodyPlainText(
                            "This email message is sent from Azure Communication Services Email using the Java SDK.");
            emailClient.beginSend(emailMessage, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
