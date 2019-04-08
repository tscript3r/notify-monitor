package pl.tscript3r.notify.monitor.crawlers.html;

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
class OLXHtmlCrawler extends AbstractHtmlCrawler implements HtmlCrawler {

    private static final String HANDLED_HOSTNAME = "olx.pl";
    private static final String CLASS_STRING = "class";
    private static final String STRONG_STRING = "strong";

    public OLXHtmlCrawler() {
        super(HANDLED_HOSTNAME);
    }

    @Override
    public List<Ad> getAds(Task task, Document document) {
        Elements adsElements = getAdsElements(document, Pattern.compile("fixed breakword\\s\\sad_*"));
        if (adsElements.isEmpty())
            throwException(NO_AD_ELEMENTS_EXCEPTION);
        return parseMainLayoutElements(task, adsElements);
    }

    private Elements getAdsElements(Document document, Pattern pattern) {
        return document.getElementsByAttributeValueMatching(CLASS_STRING,
                pattern);
    }

    private List<Ad> parseMainLayoutElements(Task task, Elements elements) {
        List<Ad> adsList = new ArrayList<>();
        elements.forEach(adElement -> {
            Ad ad = new Ad(task, adElement.select("a[href]").attr("href"));
            addPropertyWhenValueNotEmpty(ad, AdProperties.TITLE, adElement.select(STRONG_STRING)
                    .first()
                    .text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.THUMBNAIL_URL, adElement.select("img[src]")
                    .attr("src"));
            addPropertyWhenValueNotEmpty(ad, AdProperties.LOCATION, adElement.select("small[class]")
                    .attr(CLASS_STRING, "breadcrumb x-normal")
                    .select("span")
                    .first()
                    .text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.CATEGORY, adElement.select("small[class]")
                    .first()
                    .text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.PRICE, adElement.select("p")
                    .select(STRONG_STRING)
                    .text());
            adsList.add(ad);
        });
        if (adsList.isEmpty())
            throwException(NO_ADS_CREATED_EXCEPTION);
        return adsList;
    }

}
