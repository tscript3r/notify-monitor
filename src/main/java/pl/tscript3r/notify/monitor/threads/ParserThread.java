package pl.tscript3r.notify.monitor.threads;

import pl.tscript3r.notify.monitor.domain.Task;

public interface ParserThread extends Runnable {

    Boolean hasFreeSlot();

    Boolean isTask(Task task);

    Boolean removeTask(Task task);

    Boolean addTask(Task task);

    Integer getParserId();

}
