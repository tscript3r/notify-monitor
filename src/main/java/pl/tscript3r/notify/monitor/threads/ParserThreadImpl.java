package pl.tscript3r.notify.monitor.threads;

import pl.tscript3r.notify.monitor.config.ParserSettings;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;

public class ParserThreadImpl extends AbstractParserThread {

    public ParserThreadImpl(ParserFactory parserFactory, ParserSettings parserSettings) {
        super(parserFactory, parserSettings);
    }

    @Override
    public void run() {

    }
}
