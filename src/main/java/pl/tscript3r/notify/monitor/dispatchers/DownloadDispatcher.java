package pl.tscript3r.notify.monitor.dispatchers;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.DownloadException;
import pl.tscript3r.notify.monitor.threads.DownloadMonitorThread;
import pl.tscript3r.notify.monitor.threads.drivers.DownloadMonitorThreadDriver;

@Component
public class DownloadDispatcher extends AbstractDispatcher<DownloadMonitorThread> {

    private static final String RETURNED_DOCUMENTS = "returned_documents";

    public DownloadDispatcher() {
        super("downloadMonitorThread");
        status.initIntegerCounterValues(RETURNED_DOCUMENTS);
    }

    public Boolean isDownloaded(Task task) {
        DownloadMonitorThreadDriver downloadMonitorThreadDriver = getDownloadMonitorThreadDriver(task);
        if (downloadMonitorThreadDriver == null)
            return false;
        return downloadMonitorThreadDriver.isDownloaded(task);
    }

    public Document returnDocument(Task task) {
        status.incrementValue(RETURNED_DOCUMENTS);
        DownloadMonitorThreadDriver downloadMonitorThreadDriver = getDownloadMonitorThreadDriver(task);
        if(downloadMonitorThreadDriver == null)
            throw new DownloadException();
        if (downloadMonitorThreadDriver.isDownloaded(task))
            return downloadMonitorThreadDriver.returnDocument(task);
        throw new DownloadException();
    }

    private DownloadMonitorThreadDriver getDownloadMonitorThreadDriver(Task task) {
        for (DownloadMonitorThread downloadMonitorThread : monitorThreads) {
            if (downloadMonitorThread.getDriver().hasTask(task))
                return (DownloadMonitorThreadDriver) downloadMonitorThread.getDriver();
        }
        return null;
    }

}
