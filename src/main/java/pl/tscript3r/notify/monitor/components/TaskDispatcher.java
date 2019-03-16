package pl.tscript3r.notify.monitor.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskDispatcher extends AbstractDispatcher {

    public TaskDispatcher(ApplicationContext context) {
        super(log, "crawlerMonitorThread", context);
    }

}
