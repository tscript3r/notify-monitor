package pl.tscript3r.notify.monitor.api.v1.mapper;

import com.google.common.collect.Sets;
import org.junit.Test;
import pl.tscript3r.notify.monitor.api.v1.model.FilterDTO;
import pl.tscript3r.notify.monitor.filters.AdFilter;
import pl.tscript3r.notify.monitor.filters.AdFilterType;
import pl.tscript3r.notify.monitor.filters.MatchAdFilter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FilterMapperTest {

    @Test
    public void filterDTOToAdFilterWithNoCaseSensitive() {
        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setStrings(Sets.newHashSet("a", "b"));
        filterDTO.setProperty("test");
        filterDTO.setFilterType(AdFilterType.MATCH);
        AdFilter adFilter = FilterMapper.INSTANCE.filterDTOToAdFilter(filterDTO);
        assertNotNull(adFilter);
        if (!(adFilter instanceof MatchAdFilter))
            fail();
    }
}