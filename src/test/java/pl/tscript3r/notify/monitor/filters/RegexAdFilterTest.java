package pl.tscript3r.notify.monitor.filters;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexAdFilterTest {

    private static final String PROPERTY_KEY = "testProperty";
    private Map<String, String> properties;
    private RegexAdFilter regexAdFilter;

    @Before
    public void setUp() {
        properties = new HashMap<>();
    }

    @Test
    public void firstRegexTest() {
        regexAdFilter = getFilterInstance();
        assertPass("hellokitty");
        assertPass("hello kitty");
        assertNotPass("hello  kitty");
    }

    private void assertNotPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertFalse(regexAdFilter.pass(properties));
    }

    private void assertPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertTrue(regexAdFilter.pass(properties));
    }

    private RegexAdFilter getFilterInstance() {
        return new RegexAdFilter(PROPERTY_KEY,
                Sets.newHashSet("hello ?kitty", "ca*t"));
    }

    @Test
    public void secondRegexTest() {
        regexAdFilter = getFilterInstance();
        assertPass("ct");
        assertPass("caaaaaaaaaaat");
        assertNotPass("gattt");
    }

    @Test
    public void passNotWithEmptyValue() {
        regexAdFilter = getFilterInstance();
        assertNotPass("");
    }

}