const { app } = require("@azure/functions");

app.serviceBusTopic("notifyUserCancelOrder", {
  connection: "busorderweutds_SERVICEBUS",
  topicName: "cancelorder",
  subscriptionName: "send_confirm_cancel",
  handler: (message, context) => {
    context.log("Service bus topic function processed message:", message);
    // email service aufrufen
  },
});
