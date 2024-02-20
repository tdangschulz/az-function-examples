package de.tuan.timer;

import java.time.LocalDateTime;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

public class Function {
    @FunctionName("logTime")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 */5 * * * *") String timerInfo,
            final ExecutionContext context) {

        LocalDateTime now = LocalDateTime.now();
        context.getLogger().info("Die aktuelle UTC-Zeit ist: " + now.toString());
    }
}
