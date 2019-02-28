package pl.tscript3r.notify.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.threads.ParserThreadImpl;
import pl.tscript3r.notify.monitor.threads.ParserThread;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskManagerServiceImpl implements TaskManagerService {

    private final List<ParserThread> parserThreads = new ArrayList<>(10);
    private final ParserFactory parserFactory;
    private final MonitorSettings monitorSettings;

    public TaskManagerServiceImpl(ParserFactory parserFactory, MonitorSettings monitorSettings) {
        this.parserFactory = parserFactory;
        this.monitorSettings = monitorSettings;
        parserThreads.add(new ParserThreadImpl(this.parserFactory, this.monitorSettings));
    }

    private void createAdditionalParser() {
        ParserThread parserThread = new ParserThreadImpl(parserFactory, monitorSettings);
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
        if(task == null)
            return false;

        log.debug("Adding new task with id=" + task.getId());

        if (!anyFreeSlot()) {
            log.debug("No free parser slots - creating new one");
            createAdditionalParser();
        }

        if (!isTask(task))
            for (ParserThread parserThread : parserThreads)
                if (parserThread.hasFreeSlot()) {
                    log.debug("Task id=" + task.getId() +
                            " was assigned to parserThread id=" + parserThread.getParserId());
                    return parserThread.addTask(task);
                } else;
        else {
            log.error("Task with id=" + task.getId() + " is already assigned to a parserThread");
        }

        return false;
    }

    @Override
    public Boolean deleteTask(Task task) {
        if(task == null)
            return false;
        log.debug("Deleting task id=" + task.getId());
        if (isTask(task)) {
            for (ParserThread parserThread : parserThreads)
                if (parserThread.isTask(task)) {
                    log.debug("Task id=" + task.getId() + " has been found at parserThread id="
                            + parserThread.getParserId() + " and will be deleted.");
                    return parserThread.removeTask(task);
                }
        }

        log.warn("Task id=" + task.getId() + " could not be found.");
        return false;
    }

    @Override
    public Boolean isTask(Task task) {
        for (ParserThread parserThread : parserThreads)
            if (parserThread.isTask(task)) {
                log.debug("Task id=" + task.getId() + " has been found in the parserThread id=" + parserThread.getParserId());
                return true;
            }

        log.debug("Task with id=" + task.getId() + " has been not found on any parserThread");
        return false;
    }

    @Override
    public Boolean updateTask(Task task) {
        if(task == null)
            return false;
        log.debug("Updating task id=" + task.getId());
        if (!isTask(task)) {
            log.debug("Task id=" + task.getId() + " was not found, adding as new one");
            return addTask(task);
        } else {
            for (ParserThread parserThread : parserThreads)
                if (parserThread.isTask(task)) {
                    log.debug("Task with id=" + task.getId() +
                            " has been found at parserThread id=" + parserThread.getParserId() + " and will be replaced");
                    parserThread.removeTask(task);
                    return parserThread.addTask(task);
                }
        }
        throw new RuntimeException();
    }

    @Override
    public Integer getThreadsCount() {
        return parserThreads.size();
    }
}
