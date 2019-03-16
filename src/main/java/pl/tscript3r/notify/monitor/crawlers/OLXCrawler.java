package pl.tscript3r.notify.monitor.crawlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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

    @Override
    public String getHandledHostname() {
        return HANDLED_HOSTNAME;
    }

    @Override
    public List<Ad> getAds(Task task, Document document) throws CrawlerException {
        Elements adsElements = getAdsElements(document, Pattern.compile("fixed breakword\\s\\sad_*"));
        if (!adsElements.isEmpty())
            return parseMainLayoutElements(adsElements, task);

        adsElements = getAdsElements(document, Pattern.compile("offer\\s{0,8}"));
        if (!adsElements.isEmpty())
            return parseWorkLayoutElements(adsElements, task);

        throw new CrawlerException("Unexpected error appeared");
    }

    private Elements getAdsElements(Document document, Pattern pattern) {
        return document.getElementsByAttributeValueMatching("class",
                pattern);
    }

    private List<Ad> parseMainLayoutElements(Elements elements, Task task) {
        List<Ad> adsList = new ArrayList<>();
        elements.forEach(adElement -> {
            Ad ad = new Ad();
            ad.setTask(task);
            ad.setUrl(adElement.select("a[href]").attr("href"));
            ad.setTitle(adElement.select("strong")
                    .first()
                    .text());
            ad.setThumbnailUrl(adElement.select("img[src]")
                    .attr("src"));
            ad.setLocation(adElement.select("small[class]")
                    .attr("class", "breadcrumb x-normal")
                    .select("span")
                    .first()
                    .text());
            ad.setCategory(adElement.select("small[class]")
                    .first()
                    .text());
            ad.setPrice(adElement.select("p")
                    .select("strong")
                    .text());

            adsList.add(ad);
        });

        return adsList;
    }

    private List<Ad> parseWorkLayoutElements(Elements elements, Task task) {
        List<Ad> adList = new ArrayList<>();
        elements.forEach(adElement -> {
            if (StringUtils.containsIgnoreCase(adElement.attr("class"), "offer ")) {
                Ad ad = new Ad();
                ad.setTask(task);
                ad.setUrl(adElement.select("a[href]")
                        .attr("href"));
                ad.setTitle(adElement.select("div[class]")
                        .attr("class", "list-item__price")
                        .text());
                ad.setLocation(adElement.select("strong[class]")
                        .attr("strong", "list-item__location")
                        .text());
            }
        });

        return adList;
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
