package pl.tscript3r.notify.monitor.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.threads.ParserThread;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskDispatcher {

    private final ApplicationContext context;
    private final List<ParserThread> parserThreads = new ArrayList<>();

    public TaskDispatcher(ApplicationContext context) {
        this.context = context;
    }

    public void addTask(Task task) {
        findFreeParserThread().addTask(task);
    }

    private ParserThread findFreeParserThread() {
        if( !parserThreads.isEmpty())
            for (ParserThread parserThread : parserThreads) {
                if(parserThread.hasFreeSlot())
                    return parserThread;
            }

        return getNewParserThread();
    }

    private ParserThread getNewParserThread() {
        ParserThread parserThread = (ParserThread) context.getBean("parserThread");
        parserThreads.add(parserThread);
        return parserThread;
    }

}
