package pl.tscript3r.notify.monitor.crawlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.Statusable;
import pl.tscript3r.notify.monitor.utils.PackageClassScanner;

import java.util.HashMap;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CrawlerFactory implements ApplicationContextAware, Statusable {

    private static final String CRAWLER_INSTANCES_CREATED = "crawler_instances_created";

    private final Status status = Status.create(this.getClass());
    private final HashMap<String, String> hostnameCrawlers = new HashMap<>(5);
    private ApplicationContext context;

    public CrawlerFactory() {
        status.initIntegerCounterValues(CRAWLER_INSTANCES_CREATED);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
        scanLocalPackageForCrawlerImplementations(this.getClass().getPackage().toString() + ".html");
    }

    private void scanLocalPackageForCrawlerImplementations(String packagePath) {
        PackageClassScanner.scan(context, packagePath,
                Pattern.compile(".*Crawler"))
                .filterByInterface(Crawler.class)
                .filterByModifier(0) // 0 stands for package-private access
                .filterSpringComponents()
                .filterPrototypeComponents()
                .forEach(beanDefinition -> {
                    try {
                        String beanName = PackageClassScanner.getBeanName(beanDefinition.getBeanClassName());
                        Crawler crawler = (Crawler) context.getBean(beanName);
                        if (crawler.getHandledHostname() != null) {
                            if (isCompatible(crawler.getHandledHostname()))
                                throw new FatalBeanException("Found two or more crawlers for " +
                                        crawler.getHandledHostname());
                        } else
                            throw new FatalBeanException(crawler.getClass() +
                                    " returns null on getHandledHostname");
                        hostnameCrawlers.put(crawler.getHandledHostname(), beanName);
                    } catch (ClassNotFoundException e) {
                        throw new FatalBeanException(e.getMessage());
                    }
                });
    }

    public Crawler getParser(String hostname) {
        if (!isCompatible(hostname))
            throw new IncompatibleHostnameException(hostname);
        Crawler crawler = (Crawler) context.getBean(hostnameCrawlers.get(hostname));
        status.incrementValue(CRAWLER_INSTANCES_CREATED);
        return crawler;
    }

    public Boolean isCompatible(String hostname) {
        return hostnameCrawlers.containsKey(hostname);
    }

    @Override
    public Status receiveStatus() {
        return status;
    }

}
