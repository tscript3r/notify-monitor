package pl.tscript3r.notify.monitor.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;

@Slf4j
@Component
public class TaskDefaultValueSetter {

    private final Integer defaultRefreshInterval;
    private final Integer minimalInterval;
    private final Integer adContainerDefaultLimit;
    private final Integer adContainerMinimal;

    public TaskDefaultValueSetter(@Value("#{new Integer('${notify.monitor.task.defaultInterval}')}") Integer defaultRefreshInterval,
                                  @Value("#{new Integer('${notify.monitor.task.minInterval}')}") Integer minimalInterval,
                                  @Value("#{new Integer('${notify.monitor.ad.queue.defaultLimit}')}") Integer adContainerLimit,
                                  @Value("#{new Integer('${notify.monitor.ad.queue.minLimit}')}") Integer adContainerMinimal) {
        this.defaultRefreshInterval = defaultRefreshInterval;
        this.minimalInterval = minimalInterval;
        this.adContainerDefaultLimit = adContainerLimit;
        this.adContainerMinimal = adContainerMinimal;
    }

    public void validateAndSetDefaults(TaskDTO task) {
        refreshInterval(task);
        adContainer(task);
    }

    private void refreshInterval(TaskDTO task) {
        if (task.getRefreshInterval() == null || task.getRefreshInterval() < minimalInterval) {
            log.debug("Refresh interval has been overridden");
            task.setRefreshInterval(defaultRefreshInterval);
        }
    }

    private void adContainer(TaskDTO task) {
        if (task.getAdContainerLimit() == null || task.getAdContainerLimit() < adContainerMinimal) {
            log.debug("Container limit has been overridden");
            task.setAdContainerLimit(adContainerDefaultLimit);
        }
    }

}
