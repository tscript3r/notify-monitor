package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.threads.drivers.DownloadMonitorThreadDriver;

@Slf4j
@Component
@Scope("prototype")
public class DownloadMonitorThread extends AbstractMonitorThread {

    public DownloadMonitorThread(DownloadMonitorThreadDriver monitorThreadDriver,
                                 @Value("#{new Integer('${notify.monitor.threads.downloader.betweenDelay}')}") Integer betweenDelay,
                                 @Value("#{new Integer('${notify.monitor.threads.downloader.iterationDelay}')}") Integer iterationDelay) {
        super(log, monitorThreadDriver, betweenDelay, iterationDelay);
    }

}
