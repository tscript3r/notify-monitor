package pl.tscript3r.notify.monitor.crawlers;

/**
 * This interface should not be directly implemented - use HtmlCrawler or ApiCrawler.
 * If only this interface will be implemented there will be a CrawlerException thrown.
 * Has been created to get the handled hostname of his implementation mostly by the
 * CrawlerFactory.
 */
public interface Crawler {

    /**
     * @return Handled hostname in the following format: "domain.com"
     */
    String getHandledHostname();

}
