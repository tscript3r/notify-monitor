package pl.tscript3r.notify.monitor.bootstrap;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.TaskService;

import java.util.Arrays;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final TaskService taskService;
    private final Integer defaultInterval;
    private final Float adContainerMultiplier;
    private final Boolean loadBootstrap;

    public DataInitializer(TaskService taskService,
                           @Value("#{new Boolean('${notify.monitor.loadBootstrap}')}") Boolean loadBootstrap,
                           @Value("#{new Integer('${notify.monitor.task.defaultInterval}')}") Integer defaultInterval,
                           @Value("#{new Float('${notify.monitor.ad.container.multiplier}')}")
                                   Float adContainerMultiplier) {
        this.taskService = taskService;
        this.defaultInterval = defaultInterval;
        this.adContainerMultiplier = adContainerMultiplier;
        this.loadBootstrap = loadBootstrap;
    }

    @Override
    public void run(String... args) {
        if (loadBootstrap) {
            addInitialTasks();
            log.warn("Bootstrap data loaded");
        }
    }

    private void addInitialTasks() {
        taskService.saveAll(Arrays.asList(
                Task.builder()
                        .refreshInterval(defaultInterval)
                        .adContainerMultiplier(adContainerMultiplier)
                        .usersId(Sets.newHashSet(1L, 2L))
                        .url("https://www.olx.pl/oddam-za-darmo/")
                        .build(),
                Task.builder()
                        .refreshInterval(defaultInterval)
                        .adContainerMultiplier(adContainerMultiplier)
                        .usersId(Sets.newHashSet(1L))
                        .url("https://www.olx.pl/elektronika/sprzet-dvd-blu-ray/")
                        .build()
        ));
    }

}
