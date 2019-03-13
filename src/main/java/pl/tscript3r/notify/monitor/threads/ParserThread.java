package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.ParserException;
import pl.tscript3r.notify.monitor.parsers.Parser;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.*;

@Slf4j
@Component
@Scope("prototype")
public class ParserThread implements Runnable {

    private static Integer parserCounter = 0;

    private final int INITIAL_ADS_CAPACITY = 120;

    private final Thread thread;
    private final Integer parserThreadId;
    private final Integer parserCapacity;
    private final ParserFactory parserFactory;
    private Integer parserThreadCapacity;

    public ParserThread(ParserFactory parserFactory,
                        @Value("#{new Integer('${notify.monitor.threads.parserCapacity}')}") Integer parserThreadCapacity) {
        parserThreadId = parserCounter++;
        this.parserFactory = parserFactory;
        this.parserThreadCapacity = parserThreadCapacity;
        this.parserCapacity = parserThreadCapacity;

        thread = new Thread(this);
        thread.setName("pThread id=" + parserThreadId);
        start();
    }

    public Integer getParserThreadId() {
        return parserThreadId;
    }

    public Boolean hasFreeSlot() {

        // TODO: Implement

        return null;
    }

    public Boolean isTask(Task task) {

        // TODO: Implement

        return null;
    }

    public Boolean removeTask(Task task) {

        // TODO: Implement

        return null;
    }

    public Boolean addTask(Task task) {

        // TODO: implement

        return null;
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {




                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new ParserException(e.getMessage());
            }
        }
        log.warn("Thread stopped");
    }

    private Parser getParser(Task task) {

        // TODO: refactor - should not create every time new instance

        return parserFactory.getParser(HostnameExtractor.getDomain(task.getUrl()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParserThread that = (ParserThread) o;
        return Objects.equals(parserThreadId, that.getParserThreadId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(parserThreadId);
    }

}
