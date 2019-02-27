package pl.tscript3r.notify.monitor.parsers;

import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
abstract class AbstractParserThread implements ParserThread {

    private static Integer parserCounter = 0;

    private final Integer parserThreadCapacity;
    private final List<Task> taskList;
    private Integer parserId;

    public AbstractParserThread(MonitorSettings monitorSettings) {
        parserId = parserCounter++;
        parserThreadCapacity = monitorSettings.getParserThreadCapacity();
        taskList = new ArrayList<>(parserThreadCapacity + 1);
    }

    @Override
    public Boolean hasFreeSlot() {
        return taskList.size() < parserThreadCapacity;
    }

    @Override
    public Boolean hasTaskId(Long id) {
        return taskList.stream()
                .anyMatch(task -> task.getId().equals(id));
    }

    @Override
    public Boolean removeTaskById(Long id) {
        return taskList.removeIf(task -> task.getId().equals(id));
    }

    @Override
    public Boolean addTask(Task task) {
        if (hasFreeSlot() && !hasTaskId(task.getId()))
            return taskList.add(task);
        return false;
    }

    @Override
    public Integer getParserId() {
        return parserId;
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