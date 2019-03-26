package pl.tscript3r.notify.monitor.crawlers;

import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.File;
import java.io.IOException;

abstract class AbstractCrawlerTest {

    Document loadResource(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return Jsoup.parse(file, "UTF-8", "");
    }


    Task getDefaultTask() {
        return Task.builder().id(1L).usersId(Sets.newHashSet(1L)).build();
    }


}
