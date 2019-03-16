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

    public static final String DOMAIN = "domain.com";
    public static final String NOT_ADDED_DOMAIN = "notAddedDomain.com";

    @Mock
    ApplicationContext context;

    CrawlerFactory parserFactory;

    @Mock
    Crawler parser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(context.getBean(anyString())).thenReturn(parser);
        when(context.containsBeanDefinition(anyString())).thenReturn(true);
        when(context.isPrototype(anyString())).thenReturn(true);
    }

    public void defaultSetUp() {
        when(parser.getHandledHostname()).thenReturn(DOMAIN);
        parserFactory = new CrawlerFactory(context);
    }

    @Test(expected = FatalBeanException.class)
    public void constructorFailTest() {
        when(parser.getHandledHostname()).thenReturn(null);
        parserFactory = new CrawlerFactory(context);
    }

    @Test
    public void isCompatible() {
        defaultSetUp();
        assertTrue(parserFactory.isCompatible(DOMAIN));
        assertFalse(parserFactory.isCompatible(NOT_ADDED_DOMAIN));
    }

    @Test
    public void getParser() {
        defaultSetUp();
        assertEquals(parser, parserFactory.getParser(DOMAIN));
    }

    @Test(expected = IncompatibleHostnameException.class)
    public void getParserException() {
        defaultSetUp();
        parserFactory.getParser(NOT_ADDED_DOMAIN);
    }
}