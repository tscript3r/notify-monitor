package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.threads.drivers.CrawlerMonitorThreadDriver;

@Slf4j
@Component
@Scope("prototype")
public class CrawlerMonitorThread extends AbstractMonitorThread {

    public CrawlerMonitorThread(CrawlerMonitorThreadDriver monitorThreadDriver,
                                @Value("#{new Integer('${notify.monitor.threads.crawler.betweenDelay}')}") Integer betweenDelay,
                                @Value("#{new Integer('${notify.monitor.threads.crawler.iterationDelay}')}") Integer iterationDelay) {
        super(log, monitorThreadDriver, betweenDelay, iterationDelay);
    }

}
