package pl.tscript3r.notify.monitor.bootstrap;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.services.TaskService;

import java.util.Arrays;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final TaskService taskService;
    private final MonitorSettings monitorSettings;

    public DataInitializer(TaskService taskService, MonitorSettings monitorSettings) {
        this.taskService = taskService;
        this.monitorSettings = monitorSettings;
    }

    @Override
    public void run(String... args) {
        if (monitorSettings.getLoadBootstrap()) {
            TaskSettings taskSettings = TaskSettings.builder()
                    .refreshInterval(monitorSettings.getDefaultInterval())
                    .build();

            taskService.saveAll(Arrays.asList(
                    Task.builder()
                            .taskSettings(taskSettings)
                            .usersId(Sets.newHashSet(1L, 2L))
                            .url("https://www.olx.pl/oddam-za-darmo/")
                            .build(),
                    Task.builder()
                            .taskSettings(taskSettings)
                            .usersId(Sets.newHashSet(1L))
                            .url("https://www.olx.pl/elektronika/sprzet-dvd-blu-ray/")
                            .build()
            ));

            log.warn("Bootstrap data loaded");
        }
    }

}
