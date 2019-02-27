package pl.tscript3r.notify.monitor.parsers;

import pl.tscript3r.notify.monitor.domain.Task;

public interface ParserThread extends Runnable {

    Boolean hasFreeSlot();

    Boolean hasTaskId(Long id);

    Boolean removeTaskById(Long id);

    Boolean addTask(Task task);

    Integer getParserId();

}
