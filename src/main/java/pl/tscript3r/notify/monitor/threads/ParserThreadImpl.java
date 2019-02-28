package pl.tscript3r.notify.monitor.threads;

import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;

public class ParserThreadImpl extends AbstractParserThread {

    public ParserThreadImpl(ParserFactory parserFactory, MonitorSettings monitorSettings) {
        super(parserFactory, monitorSettings);
    }

    @Override
    public void run() {

    }
}
