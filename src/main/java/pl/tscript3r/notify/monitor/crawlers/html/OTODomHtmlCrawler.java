package pl.tscript3r.notify.monitor.crawlers.html;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
class OTODomHtmlCrawler extends AbstractHtmlCrawler implements HtmlCrawler {

    private static final String HANDLED_HOSTNAME = "otodom.pl";

    public OTODomHtmlCrawler() {
        super(HANDLED_HOSTNAME);
    }

    @Override
    public List<Ad> getAds(Task task, Document document) {
        Elements adElements = getAdElements(document);
        if (adElements.isEmpty())
            throwException(NO_AD_ELEMENTS_EXCEPTION);
        return createAdsFromElements(task, adElements);
    }

    private Elements getAdElements(Document document) {
        return rejectPromotedAds(document.getElementsByClass("offer-item"));
    }

    private Elements rejectPromotedAds(Elements adElements) {
        return adElements.stream()
                .filter(element ->
                        getArticleAttributeValue(element, "data-featured-name")
                                .equals("listing_no_promo")
                )
                .collect(Collectors.toCollection(Elements::new));
    }

    private String getArticleAttributeValue(Element element, String attribute) {
        return element.select("article")
                .attr(attribute);
    }

    private List<Ad> createAdsFromElements(Task task, Elements adElements) {
        List<Ad> resultAds = new ArrayList<>();
        adElements.forEach(adElement -> {
            Ad ad = new Ad(task, getAdUrl(adElement));
            String thumbnailUrl = getThumbnailUrl(adElement);
            if (isUrlValid(thumbnailUrl))
                ad.addProperty(AdProperties.THUMBNAIL_URL, getThumbnailUrl(adElement));
            else
                log.warn("Invalid thumbnail url; rejected={}", thumbnailUrl);
            addPropertyWhenValueNotEmpty(ad, AdProperties.TITLE,
                    adElement.select("span[class=offer-item-title]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.YARDAGE,
                    adElement.select("strong[class=visible-xs-block]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.PRICE,
                    adElement.select("li[class=offer-item-price]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.ROOMS,
                    adElement.select("li[class=offer-item-rooms hidden-xs]").text());
            addPropertyWhenValueNotEmpty(ad, AdProperties.LOCATION, getLocation(adElement));
            addPropertyWhenValueNotEmpty(ad, AdProperties.SELLER,
                    adElement.select("ul[class=params-small clearfix hidden-xs]").text());
            resultAds.add(ad);
        });
        if (resultAds.isEmpty())
            throw new CrawlerException(NO_ADS_CREATED_EXCEPTION);
        return resultAds;
    }

    private String getAdUrl(Element element) {
        String result = getArticleAttributeValue(element, "data-url");
        validateUrl(result);
        return result;
    }

    private String getThumbnailUrl(Element element) {
        String source = element.select("figure").attr("data-quick-gallery");
        source = source.substring(source.indexOf(":\"") + 2, source.indexOf("\"}"));
        source = source.replace("\\", "");
        source = source.substring(0, source.indexOf("\","));
        return source;
    }

    private String getLocation(Element element) {
        String result = element.select("p[class=text-nowrap hidden-xs]").text();
        result = result.substring(result.indexOf(':') + 2);
        return result;
    }

}
