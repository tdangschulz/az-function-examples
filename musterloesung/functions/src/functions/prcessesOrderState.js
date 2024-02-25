const { app, input } = require("@azure/functions");
const { TableClient } = require("@azure/data-tables");

/**
 * https://github.com/MicrosoftDocs/azure-docs/blob/main/articles/service-bus-messaging/service-bus-nodejs-how-to-use-topics-subscriptions.md
 */
app.serviceBusTopic("prcessesOrderState", {
  connection: "busorderweutds_SERVICEBUS",
  topicName: "cancelorder",
  subscriptionName: "prcesses_order_state",
  handler: async (message, context) => {
    context.log("Service bus topic function processed message:", message);

    let connectionString = process.env.AzureWebJobsStorage;

    const tableClient = TableClient.fromConnectionString(
      connectionString,
      "Orders"
    );

    const order = await tableClient.getEntity("order", message);
    order.state = "cancel";

    tableClient.upsertEntity(order);
  },
});
