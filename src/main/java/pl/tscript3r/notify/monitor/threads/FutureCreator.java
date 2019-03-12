package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.ParserException;
import pl.tscript3r.notify.monitor.parsers.Parser;
import pl.tscript3r.notify.monitor.services.DocumentDownloadService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
class FutureCreator {

    // TODO: Temporary solution, to refactor

    static Callable<Parser> getInitialExecutor(Task task, Parser parser, LinkedHashMap<Task, List<Ad>> taskAdMap,
                                               DocumentDownloadService documentDownloadService) {
        return () -> {
            task.setRefreshTime();
            taskAdMap.replace(task, parser.getAds(task, documentDownloadService.getDocument(task.getUrl())));
            return parser;
        };
    }

    static Callable<Parser> getRefreshExecutor(Task task, Parser parser, LinkedHashMap<Task, List<Ad>> taskAdMap,
                                               DocumentDownloadService documentDownloadService) {
        return () -> {
            task.setRefreshTime();
            try {
                List<Ad> ads = parser.getAds(task, documentDownloadService.getDocument(task.getUrl()));
                ads.forEach(ad -> {
                    if (!taskAdMap.get(task).contains(ad)) {
                        taskAdMap.get(task).add(ad);
                        log.error(ad.getUrl());
                    }
                });
            } catch (IOException e) {
                throw new ParserException(e.getMessage());
            }
            log.error("nico");
            return parser;
        };
    }

}
