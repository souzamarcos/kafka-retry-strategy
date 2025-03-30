package souzamarcos.demo.kafka.tasks;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binding.BindingsLifecycleController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import souzamarcos.demo.kafka.common.Bindings;

import java.time.LocalDateTime;

import static souzamarcos.demo.kafka.consumers.TransactionRetryConsumer.DELAY_TIME_IN_MINUTES;

@Slf4j
@AllArgsConstructor
@Component
public class ResumeRetryBindingTask {

    private final BindingsLifecycleController bindingsLifecycleController;

    @Scheduled(fixedRate = 5000)
    public void scheduleFixedRateTask() {
        log.info("Running ResumeRetryBindingTask");
        if (shouldResumeBinding()) {
            resumeBinding();
        }
    }

    private Boolean shouldResumeBinding() {
        var lastPausedDateTime = Bindings.getLastPausedDateTime(Bindings.TRANSACTION_RETRY_IN);
        return lastPausedDateTime != null && Bindings.getLastPausedDateTime(Bindings.TRANSACTION_RETRY_IN)
            .plusMinutes(DELAY_TIME_IN_MINUTES)
            .isBefore(LocalDateTime.now());
    }

    private void resumeBinding() {
        ## See why not startint binding
        bindingsLifecycleController.changeState(Bindings.TRANSACTION_IN.getBindingName(), BindingsLifecycleController.State.STARTED);
        log.info("Resuming binding: {}", Bindings.TRANSACTION_IN.getBindingName());
        Bindings.updateLastPausedDateTime(null);
    }
}
