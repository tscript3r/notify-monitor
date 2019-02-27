package pl.tscript3r.notify.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.parsers.threads.ParserThreadImpl;
import pl.tscript3r.notify.monitor.parsers.threads.ParserThread;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskManagerServiceImpl implements TaskManagerService {

    private final List<ParserThread> parserThreads = new ArrayList<>(10);
    private final MonitorSettings monitorSettings;

    public TaskManagerServiceImpl(MonitorSettings monitorSettings) {
        this.monitorSettings = monitorSettings;
        parserThreads.add(new ParserThreadImpl(this.monitorSettings));
    }

    private void createAdditionalParser() {
        ParserThread parserThread = new ParserThreadImpl(monitorSettings);
        parserThreads.add(parserThread);
        log.debug("Created additional parserThread with id=" + parserThread.getParserId());
    }

    private Boolean anyFreeSlot() {
        for (ParserThread parserThread : parserThreads)
            if (parserThread.hasFreeSlot()) {
                log.debug("Found free slot at parserThread id=" + parserThread.getParserId());
                return true;
            }

        log.debug("ParserThreads are full");
        return false;
    }

    @Override
    public Boolean addTask(Task task) {
        log.debug("Adding new task with id=" + task.getId());

        if (!anyFreeSlot()) {
            log.debug("No free parser slots - creating new one");
            createAdditionalParser();
        }

        if (!isTaskIdAdded(task.getId()))
            for (ParserThread parserThread : parserThreads)
                if (parserThread.hasFreeSlot()) {
                    log.debug("Task id=" + task.getId() +
                            " was assigned to parserThread id=" + parserThread.getParserId());
                    return parserThread.addTask(task);
                }

        log.error("Task with id=" + task.getId() + " is already assigned to a parserThread");
        return false;
    }

    @Override
    public Boolean deleteTaskById(Long id) {
        log.debug("Deleting task id=" + id);
        if (isTaskIdAdded(id)) {
            for (ParserThread parserThread : parserThreads)
                if (parserThread.hasTaskId(id)) {
                    log.debug("Task id=" + id + " has been found at parserThread id="
                            + parserThread.getParserId() + " and will be deleted.");
                    return parserThread.removeTaskById(id);
                }
        }

        log.warn("Task id=" + id + " could not be found.");
        return false;
    }

    @Override
    public Boolean isTaskIdAdded(Long id) {
        for (ParserThread parserThread : parserThreads)
            if (parserThread.hasTaskId(id)) {
                log.debug("Task id=" + id + " has been found in the parserThread id=" + parserThread.getParserId());
                return true;
            }

        log.debug("Task with id=" + id + " has been not found on any parserThread");
        return false;
    }

    @Override
    public Boolean updateTask(Task task) {
        log.debug("Updating task id=" + task.getId());
        if (!isTaskIdAdded(task.getId())) {
            log.debug("Task id=" + task.getId() + " was not found, adding as new one");
            return addTask(task);
        } else {
            for (ParserThread parserThread : parserThreads)
                if (parserThread.hasTaskId(task.getId())) {
                    log.debug("Task with id=" + task.getId() +
                            " has been found at parserThread id=" + parserThread.getParserId() + " and will be replaced");
                    parserThread.removeTaskById(task.getId());
                    return parserThread.addTask(task);
                }
        }

        log.error("Updating task id=" + task.getId() + " failed");
        return false;
    }
}
