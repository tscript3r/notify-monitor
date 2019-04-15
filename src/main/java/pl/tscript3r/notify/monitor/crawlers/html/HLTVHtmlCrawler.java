package pl.tscript3r.notify.monitor.crawlers.html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
class HLTVHtmlCrawler extends AbstractHtmlCrawler implements HtmlCrawler {

    private static final String HANDLED_HOSTNAME = "hltv.org";

    public HLTVHtmlCrawler() {
        super(HANDLED_HOSTNAME);
    }

    @Override
    public List<Ad> getAds(Task task, Document document) {
        Elements daysElements = getDaysElements(document);
        if (daysElements.isEmpty())
            throwException(NO_AD_ELEMENTS_EXCEPTION);
        return getAdsFromDaysElements(daysElements, task);
    }

    private Elements getDaysElements(Document document) {
        return document.getElementsByClass("results-sublist");
    }

    private List<Ad> getAdsFromDaysElements(Elements daysElements, Task task) {
        List<Ad> resultAds = new ArrayList<>();
        daysElements.forEach(dayElement -> {
            String date = getDateFromDayElement(dayElement);
            if (!date.isEmpty())
                resultAds.addAll(
                        createAdsFromDayElement(date, task, dayElement.select("div[class=result-con]"))
                );
        });
        return resultAds;
    }

    private String getDateFromDayElement(Element dayElement) {
        return cleanDate(dayElement.select("span[class=standard-headline]").text());
    }

    private String cleanDate(String date) {
        final String DATE_REFUSE = "Results for ";
        if (!date.isEmpty())
            return date.substring(date.indexOf(DATE_REFUSE) + DATE_REFUSE.length());
        else
            return "";
    }

    private List<Ad> createAdsFromDayElement(String date, Task task, Elements dayElements) {
        List<Ad> resultAds = new ArrayList<>();
        dayElements.forEach(gameElement -> {
            Ad ad = new Ad(task, getGameUrl(gameElement));
            ad.addProperty(AdProperties.DATE, date);
            resultAds.add(ad);
        });
        if (resultAds.isEmpty())
            throwException(NO_ADS_CREATED_EXCEPTION);
        return resultAds;
    }

    private String getGameUrl(Element gameElement) {
        String result = "https://www.hltv.org" + gameElement.select("a[class=a-reset]").attr("href");
        validateUrl(result);
        return result;
    }

}
