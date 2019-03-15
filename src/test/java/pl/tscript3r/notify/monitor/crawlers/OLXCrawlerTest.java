package pl.tscript3r.notify.monitor.crawlers;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.IOException;

public class OLXCrawlerTest {

    // TODO: refactor to true unit test

    OLXCrawler olxParser;

    @Before
    public void setUp() throws Exception {
        olxParser = new OLXCrawler();
    }

    @Test
    public void getAds() throws IOException {
        Task task = Task.builder().id(1L).usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/dom-ogrod/wyposazenie-wnetrz/q-obraz/").build();


    }

}