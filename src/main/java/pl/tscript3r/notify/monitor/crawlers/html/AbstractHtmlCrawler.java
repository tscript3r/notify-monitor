package pl.tscript3r.notify.monitor.crawlers.html;

import org.apache.commons.validator.routines.UrlValidator;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.util.Objects;

abstract class AbstractHtmlCrawler implements Crawler {

    static final String NO_AD_ELEMENTS_EXCEPTION = "No ads elements has been found";
    static final String NO_ADS_CREATED_EXCEPTION = "No ads has been created";
    private final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
    private final String handledHostname;

    AbstractHtmlCrawler(String handledHostname) {
        this.handledHostname = handledHostname;
    }

    @Override
    public String getHandledHostname() {
        return handledHostname;
    }

    void validateUrl(String url) {
        if (!urlValidator.isValid(url))
            throwException("URL is invalid");
    }

    Boolean isUrlValid(String url) {
        return urlValidator.isValid(url);
    }

    void addPropertyWhenValueNotEmpty(Ad ad, String property, String value) {
        if (!value.isEmpty())
            ad.addProperty(property, value);
    }

    void throwException(String message) {
        throw new CrawlerException(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crawler)) return false;
        Crawler crawler = (Crawler) o;
        return Objects.equals(this.getHandledHostname(), crawler.getHandledHostname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(handledHostname);
    }

}
