package pl.tscript3r.notify.monitor.parsers;

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
public class ParserFactory {

    private final ApplicationContext context;
    private final HashMap<String, String> hostnameParsers = new HashMap<>(5);

    public ParserFactory(ApplicationContext context) {
        this.context = context;
        PackageClassScanner.scan(context, this.getClass().getPackage().getName(),
                Pattern.compile(".*Parser"))
                .filterByInterface(Parser.class)
                .filterByModifier(0) // 0 stands for package-private access
                .filterSpringComponents()
                .filterPrototypeComponents()
                .forEach(beanDefinition -> {
                    try {
                        String beanName = PackageClassScanner.getBeanName(beanDefinition.getBeanClassName());
                        Parser parser = (Parser) context.getBean(beanName);
                        if (parser.getHandledHostname() != null) {
                            if (isCompatible(parser.getHandledHostname()))
                                throw new FatalBeanException("Found two or more parsers for " +
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

    public Parser getParser(String hostname) {
        if (!isCompatible(hostname))
            throw new IncompatibleHostnameException(hostname);
        return (Parser) context.getBean(hostnameParsers.get(hostname));
    }

    public Boolean isCompatible(String hostname) {
        return hostnameParsers.keySet()
                .stream()
                .anyMatch(listedHostname -> listedHostname.equals(hostname));
    }


}
