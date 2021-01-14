package pl.tscript3r.notify.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NotifyMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyMonitorApplication.class, args);
    }

}
