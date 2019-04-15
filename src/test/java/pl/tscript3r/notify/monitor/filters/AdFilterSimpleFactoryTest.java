package pl.tscript3r.notify.monitor.filters;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.exceptions.AdFilterException;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AdFilterSimpleFactoryTest {

    private AdFilterSimpleFactory adFilterSimpleFactory;

    @Before
    public void setUp() throws Exception {
        adFilterSimpleFactory = new AdFilterSimpleFactory();
    }

    @Test
    public void getAdFilterType() {
        assertEquals(AdFilterType.EXCLUDE,
                adFilterSimpleFactory.getAdFilterType(new ExcludeAdFilter("", false, Sets.newHashSet())));
        assertEquals(AdFilterType.INCLUDE,
                adFilterSimpleFactory.getAdFilterType(new IncludeAdFilter("", false, Sets.newHashSet())));
        assertEquals(AdFilterType.MATCH,
                adFilterSimpleFactory.getAdFilterType(new MatchAdFilter("", false, Sets.newHashSet())));
        assertEquals(AdFilterType.REGEX,
                adFilterSimpleFactory.getAdFilterType(new RegexAdFilter("", Sets.newHashSet())));
    }

    @Test(expected = AdFilterException.class)
    public void getAdFilterTypeException() {
        adFilterSimpleFactory.getAdFilterType(new AdFilter() {
            @Override
            public Boolean pass(Map<String, String> properties) {
                return null;
            }
        });
    }

    @Test
    public void getInstanceSuccess() {
        if (!(getInstance(AdFilterType.MATCH) instanceof MatchAdFilter))
            fail();
        if (!(getInstance(AdFilterType.REGEX) instanceof RegexAdFilter))
            fail();
        if (!(getInstance(AdFilterType.EXCLUDE) instanceof ExcludeAdFilter))
            fail();
        if (!(getInstance(AdFilterType.INCLUDE) instanceof IncludeAdFilter))
            fail();
    }

    private AdFilter getInstance(AdFilterType adFilterType) {
        return adFilterSimpleFactory.getInstance(adFilterType, "test", false, Sets.newHashSet("a", "b"));
    }

}