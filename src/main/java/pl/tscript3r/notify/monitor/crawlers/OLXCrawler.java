package pl.tscript3r.notify.monitor.crawlers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Component
@Scope("prototype")
class OLXCrawler implements Crawler {

    private static final String HANDLED_HOSTNAME = "olx.pl";
    private static final String CLASS_STRING = "class";
    private static final String STRONG_STRING = "strong";

    @Override
    public String getHandledHostname() {
        return HANDLED_HOSTNAME;
    }

    @Override
    public List<Ad> getAds(Task task, Document document) {
        Elements adsElements = getAdsElements(document, Pattern.compile("fixed breakword\\s\\sad_*"));
        if (!adsElements.isEmpty())
            return parseMainLayoutElements(adsElements, task);

        throw new CrawlerException("Unexpected error appeared");
    }

    private Elements getAdsElements(Document document, Pattern pattern) {
        return document.getElementsByAttributeValueMatching(CLASS_STRING,
                pattern);
    }

    private List<Ad> parseMainLayoutElements(Elements elements, Task task) {
        List<Ad> adsList = new ArrayList<>();
        elements.forEach(adElement -> {
            Ad ad = new Ad(task, adElement.select("a[href]").attr("href"));
            ad.addProperty(AdProperties.TITLE, adElement.select(STRONG_STRING)
                    .first()
                    .text());
            ad.addProperty(AdProperties.THUMBNAIL_URL, adElement.select("img[src]")
                    .attr("src"));
            ad.addProperty(AdProperties.LOCATION, adElement.select("small[class]")
                    .attr(CLASS_STRING, "breadcrumb x-normal")
                    .select("span")
                    .first()
                    .text());
            ad.addProperty(AdProperties.CATEGORY, adElement.select("small[class]")
                    .first()
                    .text());
            ad.addProperty(AdProperties.PRICE, adElement.select("p")
                    .select(STRONG_STRING)
                    .text());

            adsList.add(ad);
        });

        return adsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o instanceof Crawler) return false;
        Crawler crawler = (Crawler) o;
        return Objects.equals(HANDLED_HOSTNAME, crawler.getHandledHostname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(HANDLED_HOSTNAME);
    }

}
