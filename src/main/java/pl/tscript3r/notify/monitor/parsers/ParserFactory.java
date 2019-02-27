package pl.tscript3r.notify.monitor.parsers;

import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;

@Component
public class ParserFactory {

    public Parser getParser(String hostname) {
        throw new IncompatibleHostnameException(hostname);
       // return null;
    }

}
