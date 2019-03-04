package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.config.ParserSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
abstract class AbstractParserThread implements ParserThread {

    private static final int INITIAL_ADS_CAPACITY = 120;
    private static Integer parserCounter = 0;

    private final Integer parserThreadId;
    private final ParserFactory parserFactory;
    private final Integer parserThreadCapacity;
    private final LinkedHashMap<Task, List> taskAndDiscoveredAds;


    AbstractParserThread(ParserFactory parserFactory, ParserSettings parserSettings) {
        parserThreadId = parserCounter++;
        this.parserFactory = parserFactory;
        parserThreadCapacity = parserSettings.getParserThreadCapacity();
        taskAndDiscoveredAds = new LinkedHashMap<>(parserThreadCapacity + 1);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractParserThread that = (AbstractParserThread) o;
        return Objects.equals(parserThreadId, that.parserThreadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parserThreadId);
    }
}