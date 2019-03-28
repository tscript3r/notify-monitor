package pl.tscript3r.notify.monitor.dispatchers;

import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.threads.CrawlerMonitorThread;

@Component
public class TaskDispatcher extends AbstractDispatcher<CrawlerMonitorThread> {

    public TaskDispatcher() {
        super("crawlerMonitorThread");
    }

}
