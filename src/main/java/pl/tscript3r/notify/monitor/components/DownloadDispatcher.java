package pl.tscript3r.notify.monitor.components;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.threads.MonitorThread;
import pl.tscript3r.notify.monitor.threads.drivers.DownloadMonitorThreadDriver;

@Slf4j
@Component
public class DownloadDispatcher extends AbstractDispatcher {

    public DownloadDispatcher(ApplicationContext context) {
        super(log, "downloadMonitorThread", context);
    }

    public Boolean isDownloaded(Task task) {
        DownloadMonitorThreadDriver downloadMonitorThreadDriver = getDownloadMonitorThreadDriver(task);
        if (downloadMonitorThreadDriver == null)
            return false;
        return downloadMonitorThreadDriver.isDownloaded(task);
    }

    public Document returnDocument(Task task) {
        DownloadMonitorThreadDriver downloadMonitorThreadDriver = getDownloadMonitorThreadDriver(task);
        if (downloadMonitorThreadDriver.isDownloaded(task))
            return downloadMonitorThreadDriver.returnDocument(task);
        return null;
    }

    private DownloadMonitorThreadDriver getDownloadMonitorThreadDriver(Task task) {
        for (MonitorThread monitorThread : monitorThreads) {
            if (monitorThread.getDriver().hasTask(task))
                return (DownloadMonitorThreadDriver) monitorThread.getDriver(); // TODO: after abstract generic refactor refactor it to
        }
        return null;
    }

}
