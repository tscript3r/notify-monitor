package pl.tscript3r.notify.monitor.crawlers;

import org.apache.commons.validator.routines.UrlValidator;
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
import java.util.Objects;

@Component
@Scope("prototype")
class HLTVCrawler implements Crawler {

    private static final String HANDLED_HOSTNAME = "hltv.org";
    private static final String UNEXPECTED_ERROR_APPEARED = "Unexpected error appeared";

    @Override
    public List<Ad> getAds(Task task, Document document) {
        Elements daysElements = getDaysElements(document);
        if (daysElements.isEmpty())
            throwException(UNEXPECTED_ERROR_APPEARED);
        return getAdsFromDaysElements(daysElements, task);
    }

    private Elements getDaysElements(Document document) {
        return document.getElementsByClass("results-sublist");
    }

    private void throwException(String message) {
        throw new CrawlerException(message);
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
        return resultAds;
    }

    private String getGameUrl(Element gameElement) {
        String result = "https://www.hltv.org" + gameElement.select("a[class=a-reset").attr("href");
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (!urlValidator.isValid(result))
            throwException("URL is invalid");
        return result;
    }

    @Override
    public String getHandledHostname() {
        return HANDLED_HOSTNAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crawler)) return false;
        Crawler crawler = (Crawler) o;
        return Objects.equals(this.getHandledHostname(), crawler.getHandledHostname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(HANDLED_HOSTNAME);
    }

}
