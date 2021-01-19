package pl.tscript3r.notify.monitor.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.Statusable;

@Slf4j
@Component
public class TaskDefaultValueSetter implements Statusable {

    private static final String REFRESH_OVERRIDES_COUNT = "refresh_overrides";
    private final Status status = Status.create(this.getClass());
    private final Integer defaultRefreshInterval;
    private final Integer minimalInterval;
    private final Integer defaultEmailDuration;

    public TaskDefaultValueSetter(@Value("#{new Integer('${notify.monitor.task.defaultInterval}')}")
                                          Integer defaultRefreshInterval,
                                  @Value("#{new Integer('${notify.monitor.task.minInterval}')}")
                                          Integer minimalInterval,
                                  @Value("#{new Integer('${notify.monitor.task.defaultEmailDuration}')}")
                                          Integer defaultEmailDuration) {
        this.defaultRefreshInterval = defaultRefreshInterval;
        this.minimalInterval = minimalInterval;
        this.defaultEmailDuration = defaultEmailDuration;
        status.initIntegerCounterValues(REFRESH_OVERRIDES_COUNT);
    }

    public void validateAndSetDefaults(TaskDTO task) {
        refreshInterval(task);
        emailDuration(task);
    }

    private void refreshInterval(TaskDTO task) {
        if (task.getRefreshInterval() == null || task.getRefreshInterval() < minimalInterval) {
            status.incrementValue(REFRESH_OVERRIDES_COUNT);
            log.debug("Refresh interval has been overridden");
            task.setRefreshInterval(defaultRefreshInterval);
        }
    }

    private void emailDuration(TaskDTO task) {
        if (task.getEmailSendDuration() == null || task.getEmailSendDuration() < 60) {
            log.debug("Email send duration has been overridden");
            task.setEmailSendDuration(defaultEmailDuration);
        }
    }

    @Override
    public Status receiveStatus() {
        return status;
    }
}
