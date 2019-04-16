package pl.tscript3r.notify.monitor.dispatchers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.Statusable;
import pl.tscript3r.notify.monitor.threads.MonitorThread;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractDispatcher<T extends MonitorThread> implements ApplicationContextAware, DisposableBean,
        Statusable {

    private static final String ADDED_TASKS = "added_tasks";
    private static final String REMOVED_TASKS = "removed_tasks";

    protected final Status status = Status.create(this.getClass());
    private final String beanName;
    private final List<T> monitorThreads = new ArrayList<>();
    private ApplicationContext context;

    public AbstractDispatcher(String threadBeanName) {
        this.beanName = threadBeanName;
        status.initIntegerCounterValues(ADDED_TASKS, REMOVED_TASKS);
    }

    public void addTask(Task task) {
        status.incrementValue(ADDED_TASKS);
        findFreeMonitorThread().getDriver().addTask(task);
    }

    private T findFreeMonitorThread() {
        if (!monitorThreads.isEmpty())
            for (T monitorThread : monitorThreads) {
                if (!monitorThread.getDriver().isFull())
                    return monitorThread;
            }

        return getNewMonitorThread();
    }

    private T getNewMonitorThread() {
        log.warn("Adding new crawler thread");
        T monitorThread = (T) context.getBean(beanName);
        monitorThread.start();
        monitorThreads.add(monitorThread);
        return monitorThread;
    }

    public Boolean removeTask(Task task) {
        status.incrementValue(REMOVED_TASKS);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Override
    public void destroy() {
        monitorThreads.forEach(MonitorThread::stop);
    }

    @Override
    public Status receiveStatus() {
        return status;
    }
}
