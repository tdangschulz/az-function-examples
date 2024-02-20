package de.tuan.input;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.TableInput;

import java.util.Arrays;
import java.util.Optional;

class MyConfiguration {
    public String PartitionKey;
    public String RowKey;
    public String email;
}

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    @FunctionName("TableInputExample")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "user/{name}") HttpRequestMessage<Optional<String>> request,
            @BindingName("name") String userName,
            @TableInput(name = "config", tableName = "Configuration", partitionKey = "Configuration", rowKey = "{name}", connection = "TUAN_STORAGE_ACCOUNT") MyConfiguration[] config,

            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        var emails = Arrays.stream((config)).map(conf -> conf.email).toArray();

        return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + emails[0]).build();

    }
}
