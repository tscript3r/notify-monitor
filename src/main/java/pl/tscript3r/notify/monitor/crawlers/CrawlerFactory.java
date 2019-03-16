package pl.tscript3r.notify.monitor.crawlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.utils.PackageClassScanner;

import java.util.HashMap;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CrawlerFactory {

    private final ApplicationContext context;
    private final HashMap<String, String> hostnameParsers = new HashMap<>(5);

    public CrawlerFactory(ApplicationContext context) {
        this.context = context;
        PackageClassScanner.scan(context, this.getClass().getPackage().getName(),
                Pattern.compile(".*Crawler"))
                .filterByInterface(Crawler.class)
                .filterByModifier(0) // 0 stands for package-private access
                .filterSpringComponents()
                .filterPrototypeComponents()
                .forEach(beanDefinition -> {
                    try {
                        String beanName = PackageClassScanner.getBeanName(beanDefinition.getBeanClassName());
                        Crawler parser = (Crawler) context.getBean(beanName);
                        if (parser.getHandledHostname() != null) {
                            if (isCompatible(parser.getHandledHostname()))
                                throw new FatalBeanException("Found two or more crawlers for " +
                                        parser.getHandledHostname());
                        } else
                            throw new FatalBeanException(parser.getClass() +
                                    " returns null on getHandledHostname");
                        hostnameParsers.put(parser.getHandledHostname(), beanName);
                    } catch (ClassNotFoundException e) {
                        throw new FatalBeanException(e.getMessage());
                    }
                });
    }

    public Crawler getParser(String hostname) {
        if (!isCompatible(hostname))
            throw new IncompatibleHostnameException(hostname);
        return (Crawler) context.getBean(hostnameParsers.get(hostname));
    }

    public Boolean isCompatible(String hostname) {
        return hostnameParsers.keySet()
                .stream()
                .anyMatch(listedHostname -> listedHostname.equals(hostname));
    }


}
