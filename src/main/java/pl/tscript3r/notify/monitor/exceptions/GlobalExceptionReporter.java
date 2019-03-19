package pl.tscript3r.notify.monitor.exceptions;

import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionReporter implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // TODO: implement
        // https://www.baeldung.com/java-global-exception-handler
    }

}
