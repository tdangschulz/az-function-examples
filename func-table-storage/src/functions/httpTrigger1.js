const { app, output } = require("@azure/functions");
const { v4 } = require("uuid");
const { TableClient } = require("@azure/data-tables");

/**
 * https://learn.microsoft.com/en-us/azure/azure-functions/functions-bindings-storage-table-output?tabs=isolated-process%2Cnodejs-v4%2Ctable-api&pivots=programming-language-javascript
 * https://learn.microsoft.com/en-us/azure/azure-functions/functions-bindings-http-webhook-trigger?tabs=python-v2%2Cisolated-process%2Cnodejs-v4%2Cfunctionsv2&pivots=programming-language-javascript
 */
const tableOutput = output.table({
  tableName: "Persons",
  connection: "MyStorageConnectionAppSetting",
});

app.http("httpTrigger1", {
  methods: ["POST"],
  authLevel: "function", // x-functions-key in header oder ?code={KEY}
  extraOutputs: [tableOutput],
  route: "persons/{uuid?}",
  handler: async (request, context) => {
    context.log(`Http function processed request for url "${request.url}"`);

    const body = await request.json();
    context.log(body);
    const item = {
      ...body,
      partitionKey: "persons",
      rowKey: request.params.uuid ?? v4(),
      __timestamp: Date.now(),
    };

    if (request.params.uuid) {
      let connectionString = process.env.MyStorageConnectionAppSetting;

      const tableClient = TableClient.fromConnectionString(
        connectionString,
        "Persons"
      );

      context.log(item);

      await tableClient.upsertEntity(item, "Replace");
    } else {
      context.extraOutputs.set(tableOutput, [item]);
    }

    return { headers: [], status: 201, body: JSON.stringify(item) };
  },
});
