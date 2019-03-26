package pl.tscript3r.notify.monitor.crawlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CrawlerFactoryTest {

    private static final String RANDOM_DOMAIN = "random-%s-domain.com";
    private static final String NOT_ADDED_DOMAIN = "notAddedDomain.com";

    private Integer randomDomainCounter;

    @Mock
    ApplicationContext context;

    @Mock
    Crawler crawler;

    private CrawlerFactory crawlerFactory;

    @Before
    public void setUp() {
        randomDomainCounter = 0;
        MockitoAnnotations.initMocks(this);
        when(context.getBean(anyString())).thenReturn(crawler);
        when(context.containsBeanDefinition(anyString())).thenReturn(true);
        when(context.isPrototype(anyString())).thenReturn(true);
    }

    private void defaultSetUp() {
        randomDomainCounter = 0;
        when(crawler.getHandledHostname()).thenAnswer((a) -> getGeneratedDomainName());
        crawlerFactory = new CrawlerFactory();
        crawlerFactory.setApplicationContext(context);
        randomDomainCounter = 0;
    }

    private String getGeneratedDomainName() {
        randomDomainCounter++;
        return String.format(RANDOM_DOMAIN, randomDomainCounter.toString());
    }

    @Test(expected = FatalBeanException.class)
    public void constructorFailTest() {
        when(crawler.getHandledHostname()).thenReturn(null);
        crawlerFactory = new CrawlerFactory();
        crawlerFactory.setApplicationContext(context);
    }

    @Test
    public void isCompatible() {
        defaultSetUp();
        when(crawler.getHandledHostname()).thenReturn(getGeneratedDomainName());
        assertTrue(crawlerFactory.isCompatible(getGeneratedDomainName()));
        assertFalse(crawlerFactory.isCompatible(NOT_ADDED_DOMAIN));
    }

    @Test
    public void getParser() {
        defaultSetUp();
        when(crawler.getHandledHostname()).thenReturn(getGeneratedDomainName());
        assertEquals(crawler, crawlerFactory.getParser(getGeneratedDomainName()));
    }

    @Test(expected = IncompatibleHostnameException.class)
    public void getParserException() {
        defaultSetUp();
        crawlerFactory.getParser(NOT_ADDED_DOMAIN);
    }

    @Test(expected = FatalBeanException.class)
    public void duplicatedDomainCrawlerImplementationsException() {
        when(crawler.getHandledHostname()).thenReturn(RANDOM_DOMAIN);
        crawlerFactory = new CrawlerFactory();
        crawlerFactory.setApplicationContext(context);
    }

    @Test
    public void receiveStatusNotNull() {
        defaultSetUp();
        assertNotNull(crawlerFactory.receiveStatus());
    }
}