package pl.tscript3r.notify.monitor.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.TaskService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
        Set<Long> usersId = new HashSet<>();
        usersId.add(1L);
        usersId.add(2L);
        taskService.saveAll(Arrays.asList(
                Task.builder()
                        .refreshInterval(defaultInterval)
                        .adContainerMultiplier(adContainerMultiplier)
                        .usersId(usersId)
                        .url("https://www.olx.pl/oddam-za-darmo/")
                        .build(),
                Task.builder()
                        .refreshInterval(defaultInterval)
                        .adContainerMultiplier(adContainerMultiplier)
                        .usersId(usersId)
                        .url("https://www.olx.pl/elektronika/sprzet-dvd-blu-ray/")
                        .build()
        ));
    }

}
