package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Scope("prototype")
public class ParserThreadImpl implements ParserThread {

    private static Integer parserCounter = 0;

    private final int INITIAL_ADS_CAPACITY = 120;

    private final Thread thread;
    private final Integer parserThreadId;
    private final ParserFactory parserFactory;
    private final LinkedHashMap<Task, List> taskAndDiscoveredAds;
    private Integer parserThreadCapacity = 5;

    public ParserThreadImpl(ParserFactory parserFactory) {
        parserThreadId = parserCounter++;
        this.parserFactory = parserFactory;
        thread = new Thread(this);
        thread.setName("pThread id=" + parserThreadId);
        taskAndDiscoveredAds = new LinkedHashMap<>(parserThreadCapacity + 1);
    }

    @Value("#{new Integer('${notify.monitor.threads.parserCapacity}')}")
    public void setParserThreadCapacity(Integer parserThreadCapacity) {
        this.parserThreadCapacity = parserThreadCapacity;
    }

    @Override
    public Boolean hasFreeSlot() {
        Boolean result = taskAndDiscoveredAds.size() < parserThreadCapacity;
        if (result)
            log.debug("ParserThread id=" + this.parserThreadId + " has available slot");
        else
            log.debug("ParserThread id=" + this.parserThreadId + " has no free slots");

        return result;
    }

    @Override
    public Integer getParserThreadId() {
        return parserThreadId;
    }

    @Override
    public Boolean isTask(Task task) {
        Boolean result = taskAndDiscoveredAds.containsKey(task);
        if (result)
            log.debug("ParserThread id=" + this.parserThreadId
                    + " contains task id=" + task.getId());
        else
            log.debug("ParserThread id=" + this.parserThreadId
                    + " does not contain task id=" + task.getId());

        return result;
    }

    @Override
    public Boolean removeTask(Task task) {
        Boolean result = taskAndDiscoveredAds.remove(task) != null;
        if (result)
            log.debug("Task id=" + task.getId()
                    + " has been removed from ParserThread id=" + this.parserThreadId);
        else
            log.debug("Task id=" + task.getId()
                    + " could not be removed from ParserThread id=" + this.parserThreadId);
        return result;
    }

    @Override
    public Boolean addTask(Task task) {
        if (task != null && hasFreeSlot() && !isTask(task)) {
            taskAndDiscoveredAds.put(task, new ArrayList(INITIAL_ADS_CAPACITY));
            log.debug("Task id=" + task.getId() +
                    " has been added to ParserThread id=" + this.parserThreadId);
            return true;
        }
        log.debug("Task id=" + task.getId()
                + "could not be added to ParserThread id=" + this.parserThreadId);
        return false;
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {

            try {
                // TODO: implement, concept: Future<Document>

                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        log.warn("Thread stopped");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParserThreadImpl that = (ParserThreadImpl) o;
        return Objects.equals(parserThreadId, that.parserThreadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parserThreadId);
    }

}
