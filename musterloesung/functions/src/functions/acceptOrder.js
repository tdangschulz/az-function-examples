const { app, output } = require("@azure/functions");

const tableOutput = output.table({
  tableName: "Orders",
  connection: "AzureWebJobsStorage",
});

const queueOutput = output.storageQueue({
  queueName: "notifyQueue",
  connection: "AzureWebJobsStorage",
});

app.storageQueue("accept-order", {
  queueName: "order-accepted-queue",
  connection: "AzureWebJobsStorage",
  extraOutputs: [tableOutput],
  handler: (queueItem, context) => {
    const item = {
      partitionKey: "order",
      rowKey: makeid(10),
      ...queueItem,
    };
    context.extraOutputs.set(tableOutput, [item]);

    context.extraOutputs.set(queueOutput, JSON.stringify(queueItem));

    context.log(queueItem);
  },
});

function makeid(length) {
  let result = "";
  const characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  const charactersLength = characters.length;
  let counter = 0;
  while (counter < length) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
    counter += 1;
  }
  return result;
}
