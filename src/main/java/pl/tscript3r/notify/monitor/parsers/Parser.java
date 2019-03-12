package pl.tscript3r.notify.monitor.parsers;

import org.jsoup.nodes.Document;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.ParserException;

import java.util.List;

public interface Parser {

    /**
     * Returns all currently found ads from the url
     */
    List<Ad> getAds(Task task, Document document) throws ParserException;

    /**
     * @return Handled by itself hostname by the example format: "olx.pl"
     */
    String getHandledHostname();

}
