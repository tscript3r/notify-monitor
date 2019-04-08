package pl.tscript3r.notify.monitor.crawlers.html;

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
import java.util.regex.Pattern;

@Slf4j
@Component
@Scope("prototype")
class OTOMotoHtmlCrawler extends AbstractHtmlCrawler implements HtmlCrawler {

    private static final String HANDLED_HOSTNAME = "otomoto.pl";

    public OTOMotoHtmlCrawler() {
        super(HANDLED_HOSTNAME);
    }

    @Override
    public List<Ad> getAds(Task task, Document document) {
        Elements adsElements = getAdsElements(document, Pattern.compile("adListingItem offer-item is-row.*"));
        if (adsElements.isEmpty())
            throwException(NO_AD_ELEMENTS_EXCEPTION);
        return createAds(adsElements, task);
    }

    private Elements getAdsElements(Document document, Pattern pattern) {
        return document.getElementsByAttributeValueMatching("class",
                pattern);
    }

    private List<Ad> createAds(Elements elements, Task task) {
        List<Ad> resultAds = new ArrayList<>();
        elements.forEach(element -> {
            String url = element.select("a[href]").attr("href");
            validateUrl(url);
            Ad ad = new Ad(task, url);
            String thumbnailUrl = element.select("img[class]").attr("data-src");
            if (isUrlValid(thumbnailUrl))
                addPropertyWhenValueNotEmpty(ad, AdProperties.THUMBNAIL_URL, thumbnailUrl);
            else
                log.warn("Invalid thumbnail url; rejected={}", thumbnailUrl);
            addPropertyWhenValueNotEmpty(ad, AdProperties.TITLE, element.select("a[data-ad-id]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.PRICE, element.select("span[class=offer-price__number]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.LOCATION, element.select("span[class=offer-item__location]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.PRODUCTION_YEAR, element.select("li[data-code=year]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.MILEAGE, element.select("li[data-code=mileage]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.ENGINE_CAPACITY, element.select("li[data-code=engine_capacity]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.FUEL_TYPE, element.select("li[data-code=fuel_type]").text());
            resultAds.add(ad);
        });
        if (resultAds.isEmpty())
            throw new CrawlerException(NO_ADS_CREATED_EXCEPTION);
        return resultAds;
    }

}
