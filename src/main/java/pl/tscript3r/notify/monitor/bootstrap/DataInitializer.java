package pl.tscript3r.notify.monitor.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.UserDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.TaskService;
import pl.tscript3r.notify.monitor.services.UserService;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final TaskService taskService;
    private final UserService userService;
    private final Integer defaultInterval;
    private final Float adContainerMultiplier;
    private final Boolean loadBootstrap;

    public DataInitializer(TaskService taskService,
                           UserService userService,
                           @Value("#{new Boolean('${notify.monitor.loadBootstrap}')}") Boolean loadBootstrap,
                           @Value("#{new Integer('${notify.monitor.task.defaultInterval}')}") Integer defaultInterval,
                           @Value("#{new Float('${notify.monitor.ad.container.multiplier}')}")
                                   Float adContainerMultiplier) {
        this.userService = userService;
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
        usersId.forEach(id -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(id);
            userDTO.setEmail("random@random.com");
            userService.add(userDTO);
        });
        taskService.saveAll(Arrays.asList(
                Task.builder()
                        .refreshInterval(300)
                        .adContainerMultiplier(adContainerMultiplier)
                        .usersId(usersId)
                        .url("https://www.olx.pl/oddam-za-darmo/")
                        .emailSendDuration(Duration.ofSeconds(60))
                        .build(),
                Task.builder()
                        .refreshInterval(300)
                        .adContainerMultiplier(adContainerMultiplier)
                        .usersId(usersId)
                        .emailSendDuration(Duration.ofSeconds(60))
                        .url("https://www.olx.pl/elektronika/sprzet-dvd-blu-ray/")
                        .build()
        ));
    }

}
