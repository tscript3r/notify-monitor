package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.threads.drivers.DownloadMonitorThreadDriver;

@Slf4j
@Component
@Scope("prototype")
public class DownloadMonitorThread extends AbstractMonitorThread {

    public DownloadMonitorThread(DownloadMonitorThreadDriver monitorThreadDriver) {
        super(log, monitorThreadDriver);
    }

}
