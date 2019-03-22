package pl.tscript3r.notify.monitor.filters;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IncludeAdFilterTest {

    private static final String PROPERTY_KEY = "testProperty";
    private Map<String, String> properties;
    private IncludeAdFilter includeAdFilter;

    @Before
    public void setUp() throws Exception {
        properties = new HashMap<>();
    }

    @Test
    public void passWithPropertyAndValue_NotCaseSensitive() {
        includeAdFilter = getFilterInstance(false);
        assertPass("include");
        assertPass("required");
        assertPass("123required321");
        assertNotPass("not_inside");
        assertNotPass("not_added");
    }


    private void assertNotPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertFalse(includeAdFilter.pass(properties));
    }

    private void assertPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertTrue(includeAdFilter.pass(properties));
    }

    private IncludeAdFilter getFilterInstance(Boolean caseSensitive) {
        return new IncludeAdFilter(PROPERTY_KEY, caseSensitive,
                Sets.newHashSet("include", "required"));
    }

    @Test
    public void passWithPropertyAndValue_CaseSensitive() {
        includeAdFilter = getFilterInstance(true);
        assertPass("include");
        assertPass("required");
        assertPass("123required321");
        assertNotPass("iNcLuDe");
        assertNotPass("Required");
        assertNotPass("not_inside");
        assertNotPass("not_added");
    }

    @Test
    public void passNotWithEmptyValue() {
        includeAdFilter = getFilterInstance(true);
        assertNotPass("");
    }

}