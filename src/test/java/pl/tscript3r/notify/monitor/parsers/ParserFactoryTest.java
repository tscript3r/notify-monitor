package pl.tscript3r.notify.monitor.parsers;

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

public class ParserFactoryTest {

    public static final String DOMAIN = "domain.com";
    public static final String NOT_ADDED_DOMAIN = "notAddedDomain.com";

    @Mock
    ApplicationContext context;

    ParserFactory parserFactory;

    @Mock
    Parser parser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(context.getBean(anyString())).thenReturn(parser);
        when(context.containsBeanDefinition(anyString())).thenReturn(true);
        when(context.isPrototype(anyString())).thenReturn(true);
    }

    public void defaultSetUp() {
        when(parser.getHandledHostname()).thenReturn(DOMAIN);
        parserFactory = new ParserFactory(context);
    }

    @Test(expected = FatalBeanException.class)
    public void constructorFailTest() {
        when(parser.getHandledHostname()).thenReturn(null);
        parserFactory = new ParserFactory(context);
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