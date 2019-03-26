package pl.tscript3r.notify.monitor.crawlers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
@Scope("prototype")
class OTOMotoCrawler implements Crawler {

    private static final String HANDLED_HOSTNAME = "otomoto.pl";

    @Override
    public List<Ad> getAds(Task task, Document document) {
        Elements adsElements = getAdsElements(document, Pattern.compile("adListingItem offer-item is-row.*"));
        return createAds(adsElements, task);
    }

    private Elements getAdsElements(Document document, Pattern pattern) {
        return document.getElementsByAttributeValueMatching("class",
                pattern);
    }

    private List<Ad> createAds(Elements elements, Task task) {
        List<Ad> resultAds = new ArrayList<>();
        elements.forEach(element -> {
            Ad ad = new Ad(task, element.select("a[href]").attr("href"));
            ad.addProperty(AdProperties.THUMBNAIL_URL, element.select("img[class]")
                    .attr("data-src"));
            ad.addProperty(AdProperties.TITLE, element.select("a[data-ad-id]").text());
            ad.addProperty(AdProperties.PRICE, element.select("span[class=offer-price__number]").text());
            ad.addProperty(AdProperties.LOCATION, element.select("span[class=offer-item__location]").text());
            ad.addProperty(AdProperties.PRODUCTION_YEAR, element.select("li[data-code=year]").text());
            ad.addProperty(AdProperties.MILEAGE, element.select("li[data-code=mileage]").text());
            ad.addProperty(AdProperties.ENGINE_CAPACITY, element.select("li[data-code=engine_capacity]").text());
            ad.addProperty(AdProperties.FUEL_TYPE, element.select("li[data-code=fuel_type]").text());
            resultAds.add(ad);
        });
        return resultAds;
    }

    @Override
    public String getHandledHostname() {
        return HANDLED_HOSTNAME;
    }
}
