package pl.tscript3r.notify.monitor.dispatchers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.threads.CrawlerMonitorThread;

@Slf4j
@Component
public class TaskDispatcher extends AbstractDispatcher<CrawlerMonitorThread> {

    public TaskDispatcher() {
        super(log, "crawlerMonitorThread");
    }

}
