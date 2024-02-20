package de.tuan.blob;

import java.nio.charset.StandardCharsets;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobOutput;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.StorageAccount;

/**
 * https://learn.microsoft.com/en-us/azure/azure-functions/functions-bindings-storage-blob-output?tabs=python-v2%2Cisolated-process%2Cnodejs-v4&pivots=programming-language-java
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    @FunctionName("copyBlob")
    @StorageAccount("TUAN_STORAGE_ACCOUNT")
    public void copyBlobHttp(
            @BlobTrigger(name = "file", dataType = "binary", path = "blob-input/{name}") byte[] content,
            @BindingName("name") String blobName,
            @BlobOutput(name = "target", path = "blob-output/{name}-copy") OutputBinding<String> outputItem,
            final ExecutionContext context) {
        // Save blob to outputItem

        final int MAX_SIZE = 200 * 1024; // 200 KB in Bytes
        if (content.length > MAX_SIZE) {
            outputItem.setValue(new String(content, StandardCharsets.UTF_8));
        }

    }
}
