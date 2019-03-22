package pl.tscript3r.notify.monitor.filters;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExcludeAdFilterTest {

    private static final String PROPERTY_KEY = "testProperty";
    private Map<String, String> properties;
    private ExcludeAdFilter excludeAdFilter;

    @Before
    public void setUp() {
        properties = new HashMap<>();
    }

    @Test
    public void passWithPropertyAndValue_NotCaseSensitive() {
        excludeAdFilter = getFilterInstance(false);
        assertPass("passValue");
        assertNotPass("rejected");
        assertNotPass("excluded");
        assertNotPass("reJecTeD");
        assertNotPass("eXclUdeD");
        assertNotPass("123excluded321");
        assertNotPass("123reJecTeD321");
    }

    private void assertNotPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertFalse(excludeAdFilter.pass(properties));
    }

    private void assertPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertTrue(excludeAdFilter.pass(properties));
    }

    private ExcludeAdFilter getFilterInstance(Boolean caseSensitive) {
        return new ExcludeAdFilter(PROPERTY_KEY, caseSensitive,
                Sets.newHashSet("rejected", "excluded"));
    }

    @Test
    public void passWithPropertyAndValue_CaseSensitive() {
        excludeAdFilter = getFilterInstance(true);
        assertPass("passValue");
        assertNotPass("rejected");
        assertNotPass("excluded");
        assertNotPass("123rejected");
        assertNotPass("excluded321");
        assertPass("reJecTeD");
        assertPass("eXclUdeD");
    }

    @Test
    public void passWithEmptyValue() {
        excludeAdFilter = getFilterInstance(true);
        assertPass("");
    }

}