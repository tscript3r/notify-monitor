package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.config.ParserSettings;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;

@Slf4j
public class ParserThreadImpl extends AbstractParserThread {

    public ParserThreadImpl(ParserFactory parserFactory, ParserSettings parserSettings) {
        super(parserFactory, parserSettings);
    }

    @Override
    public void run() {
        while( !thread.isInterrupted()) {

            try {
                // TODO: implement, concept: Future<Document>
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        log.warn("Thread stopped");
    }

}
