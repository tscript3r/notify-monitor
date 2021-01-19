package pl.tscript3r.notify.monitor.crawlers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.components.JsoupDocumentDownloader;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class NieruchomosciOnlineHtmlCrawler extends AbstractHtmlCrawler implements Crawler {

    private static final String HANDLED_HOSTNAME = "nieruchomosci-online.pl";

    public NieruchomosciOnlineHtmlCrawler(JsoupDocumentDownloader jsoupDocumentDownloader) {
        super(HANDLED_HOSTNAME, jsoupDocumentDownloader);
    }

    @Override
    public List<Ad> getAds(Task task) throws IOException {
        Document document = getDocumentContainingNonPromotedAds(task);
        return map2Ads(task, getAdElementsList(document));
    }

    private Document getDocumentContainingNonPromotedAds(Task task) throws IOException {
        Document document = getDocument(task.getUrl());
        if (!containsNonPromotedAds(document))
            document = getNextPage(document);
        if (!containsNonPromotedAds(document))
            throwException("Could not find non promoted ads");
        return document;
    }

    private Document getNextPage(Document document) throws IOException {
        Elements elements = document.getElementsByAttributeValue("class", "next");
        String url = elements.select("a[href]").attr("href");
        if (url.isEmpty())
            throwException("Invalid next page url");
        return getDocument(url);
    }

    private List<Element> getAdElementsList(Document document) {
        List<Element> adElements = filterPromotedAds(document.getElementsByClass("column-container"));
        if (adElements.isEmpty())
            throwException("Could not find any ads");
        return getAdElementsList(adElements);
    }

    private List<Element> filterPromotedAds(Elements elements) {
        return elements.stream()
                .filter(element -> element.getElementsByAttributeValue("data-pie", "prime").size() == 0)
                .collect(Collectors.toList());
    }

    private List<Element> getAdElementsList(List<Element> adElements) {
        return adElements.stream()
                .map(element -> element.getElementsByAttribute("data-id"))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Boolean containsNonPromotedAds(Document document) {
        return document.getElementsByAttributeValue("id", "pie_normal").size() > 0;
    }

    private List<Ad> map2Ads(Task task, List<Element> adElementsList) {
        return adElementsList.stream()
                .map(element -> {
                    Ad ad = new Ad(task, element.select("a[href]").attr("href"));
                    addPropertyWhenValueNotEmpty(ad, AdProperties.THUMBNAIL_URL,
                            element.select("img[src]").attr("src"));
                    addPropertyWhenValueNotEmpty(ad, AdProperties.TITLE,
                            element.select("a[href]").text());
                    addPropertyWhenValueNotEmpty(ad, AdProperties.PRICE,
                            element.select("p.title-a, .primary-display").select("span").first().text());
                    return ad;
                })
                .collect(Collectors.toList());
    }

}
