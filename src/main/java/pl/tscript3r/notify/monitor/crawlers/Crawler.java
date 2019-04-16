package pl.tscript3r.notify.monitor.crawlers;

import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.IOException;
import java.util.List;

public interface Crawler {

    /**
     * @return Handled hostname in the following format: "domain.com"
     */
    String getHandledHostname();

    List<Ad> getAds(Task task) throws IOException;

}
