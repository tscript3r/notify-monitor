package pl.tscript3r.notify.monitor.api.v1.mapper;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.api.v1.model.AdFilterDTO;
import pl.tscript3r.notify.monitor.exceptions.AdFilterException;
import pl.tscript3r.notify.monitor.filters.*;

import java.util.Map;

import static org.junit.Assert.*;

public class AdFilterMapperTest {

    private AdFilterMapper adFilterMapper;

    @Before
    public void setUp() {
        adFilterMapper = new AdFilterMapper(new AdFilterSimpleFactory());
    }

    @Test
    public void filterDTOToAdFilterWithNoCaseSensitive() {
        AdFilterDTO adFilterDTO = new AdFilterDTO();
        adFilterDTO.setStrings(Sets.newHashSet("a", "b"));
        adFilterDTO.setProperty("test");
        adFilterDTO.setFilterType(AdFilterType.MATCH);
        AdFilter adFilter = adFilterMapper.adFilterDTOToAdFilter(adFilterDTO);
        assertNotNull(adFilter);
        if (!(adFilter instanceof MatchAdFilter))
            fail();
    }

    @Test
    public void regexAdFilterToAdFilterDTO() {
        RegexAdFilter regexAdFilter = new RegexAdFilter("test", Sets.newHashSet("a", "b"));
        AdFilterDTO adFilterDTO = adFilterMapper.adFilterToAdFilterDTO(regexAdFilter);
        assertEquals(regexAdFilter.getPropertyKey(), adFilterDTO.getProperty());
        assertEquals(2, adFilterDTO.getStrings().size());
    }

    @Test(expected = AdFilterException.class)
    public void adFilterToAdFilterDTOException() {
        adFilterMapper.adFilterToAdFilterDTO(new AdFilter() {
            @Override
            public Boolean pass(Map<String, String> properties) {
                return null;
            }
        });
    }

}