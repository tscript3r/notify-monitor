package pl.tscript3r.notify.monitor.filters;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatchAdFilterTest {

    private static final String PROPERTY_KEY = "testProperty";
    private Map<String, String> properties;
    private MatchAdFilter matchAdFilter;

    @Before
    public void setUp() throws Exception {
        properties = new HashMap<>();
    }

    @Test
    public void passWithPropertyAndValue_NotCaseSensitive() {
        matchAdFilter = getFilterInstance(false);
        assertPass("matching");
        assertPass("or_this_matching");
        assertPass("MatcHinG");
        assertPass("or_THIS_matching");
        assertNotPass("not_matching");
        assertNotPass("matching_not");
        assertNotPass("123matching321");
    }

    private void assertNotPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertFalse(matchAdFilter.pass(properties));
    }

    private void assertPass(String value) {
        properties.put(PROPERTY_KEY, value);
        assertTrue(matchAdFilter.pass(properties));
    }

    private MatchAdFilter getFilterInstance(Boolean caseSensitive) {
        return new MatchAdFilter(PROPERTY_KEY, caseSensitive,
                Sets.newHashSet("matching", "or_this_matching"));
    }

    @Test
    public void passWithPropertyAndValue_CaseSensitive() {
        matchAdFilter = getFilterInstance(true);
        assertPass("matching");
        assertPass("or_this_matching");
        assertNotPass("MatcHinG");
        assertNotPass("or_THIS_matching");
        assertNotPass("123matching");
        assertNotPass("or_this_matching321");
    }

    @Test
    public void passNotWithEmptyValue() {
        matchAdFilter = getFilterInstance(true);
        assertNotPass("");
    }

}