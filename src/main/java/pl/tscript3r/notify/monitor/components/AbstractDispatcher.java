package pl.tscript3r.notify.monitor.components;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.threads.MonitorThread;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDispatcher {

    // TODO: Refactor to generic
    private final Logger log;
    private final String beanName;
    private final ApplicationContext context;
    protected final List<MonitorThread> monitorThreads = new ArrayList<>();

    public AbstractDispatcher(Logger log, String threadBeanName, ApplicationContext context) {
        this.log = log;
        this.beanName = threadBeanName;
        this.context = context;
    }

    public void addTask(Task task) {
        log.debug("Adding task id=" + task.getId());
        findFreeMonitorThread().getDriver().addTask(task);
    }

    private MonitorThread findFreeMonitorThread() {
        if (!monitorThreads.isEmpty())
            for (MonitorThread monitorThread : monitorThreads) {
                if (!monitorThread.getDriver().isFull())
                    return monitorThread;
            }

        return getNewMonitorThread();
    }

    private MonitorThread getNewMonitorThread() {
        MonitorThread monitorThread = (MonitorThread) context.getBean(beanName);
        monitorThread.start();
        monitorThreads.add(monitorThread);
        return monitorThread;
    }

    public Boolean removeTask(Task task) {
        for (MonitorThread monitorThread : monitorThreads)
            if (monitorThread.getDriver().hasTask(task))
                return monitorThread.getDriver().removeTask(task);
        return false;
    }

    public Boolean containsTask(Task task) {
        for (MonitorThread monitorThread : monitorThreads)
            if (monitorThread.getDriver().hasTask(task))
                return true;
        return false;
    }
}
