package pl.tscript3r.notify.monitor.parsers;

import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.ParserException;

import java.io.IOException;
import java.util.List;

public interface Parser {

    /**
     * Returns all currently found ads from the url
     */
    List<Ad> getAds(Task task) throws IOException, ParserException;

}
