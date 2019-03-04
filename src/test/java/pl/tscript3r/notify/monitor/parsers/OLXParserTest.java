package pl.tscript3r.notify.monitor.parsers;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import pl.tscript3r.notify.monitor.config.ParserSettings;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.IOException;

import static org.junit.Assert.*;

public class OLXParserTest {

    OLXParser olxParser;

    ParserSettings parserSettings;

    @Before
    public void setUp() throws Exception {
        parserSettings = new ParserSettings(3, 5000, 5, 5120, "Mozilla/5.0 Chrome/26.0.1410.64 Safari/537.31", true);

        olxParser = new OLXParser(parserSettings);
    }

    @Test
    public void getAds() throws IOException {
        Task task = Task.builder().id(1L).usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/dom-ogrod/wyposazenie-wnetrz/q-obraz/").build();

        olxParser.getAds(task).forEach(ad -> {
            System.out.println(ad);
        });
    }

}