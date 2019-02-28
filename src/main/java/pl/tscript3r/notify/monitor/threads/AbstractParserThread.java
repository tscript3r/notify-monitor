package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.parsers.Parser;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.*;

@Slf4j
abstract class AbstractParserThread implements ParserThread {

    private static Integer parserCounter = 0;

    private final ParserFactory parserFactory;
    private final Integer parserThreadCapacity;
    private final Map<Task, Parser> taskList;
    private Integer parserId;

    public AbstractParserThread(ParserFactory parserFactory, MonitorSettings monitorSettings) {
        parserId = parserCounter++;
        this.parserFactory = parserFactory;
        parserThreadCapacity = monitorSettings.getParserThreadCapacity();
        taskList = new HashMap<>(parserThreadCapacity + 1);
    }

    @Override
    public Boolean hasFreeSlot() {
        return taskList.size() < parserThreadCapacity;
    }

    @Override
    public Integer getParserId() {
        return parserId;
    }

    @Override
    public Boolean isTask(Task task) {
        return taskList.containsKey(task);
    }

    @Override
    public Boolean removeTask(Task task) {
        return taskList.remove(task) != null;
    }

    @Override
    public Boolean addTask(Task task) {
        if(task != null && hasFreeSlot() && !isTask(task)) {
            Parser parser = parserFactory.getParser(
                    HostnameExtractor.getDomain(task.getUrl()));
            if(parser != null) {
                taskList.put(task, parser);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractParserThread that = (AbstractParserThread) o;
        return Objects.equals(taskList, that.taskList) &&
                Objects.equals(parserId, that.parserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskList, parserId);
    }

}