package pl.tscript3r.notify.monitor.crawlers;

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
class HLTVCrawler extends AbstractCrawler implements Crawler {

    private static final String HANDLED_HOSTNAME = "hltv.org";

    public HLTVCrawler() {
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
            resultAds.addAll(
                    createAdsFromDayElement(date, task, dayElement.select("div[class=result-con]"))
            );
        });
        return resultAds;
    }

    private String getDateFromDayElement(Element dayElement) {
        String result = cleanDate(dayElement.select("span[class=standard-headline]").text());
        if (result.isEmpty())
            throwException("Cannot get date elements");
        return result;
    }

    private String cleanDate(String date) {
        final String DATE_REFUSE = "Results for ";
        if (!date.contains(DATE_REFUSE))
            throwException("Days date format has been changed");
        return date.substring(date.indexOf(DATE_REFUSE) + DATE_REFUSE.length());
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
