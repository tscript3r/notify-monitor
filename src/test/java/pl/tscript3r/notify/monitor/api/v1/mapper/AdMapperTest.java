package pl.tscript3r.notify.monitor.api.v1.mapper;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import static org.junit.Assert.assertEquals;

public class AdMapperTest {

    private static final long ID1 = 1L;
    private static final long ID2 = 2L;
    private static final long ID3 = 3L;
    private static final String URL = "https://www.olx.pl/test/";
    private static final String KEY = "test";
    private static final String VALUE = "testable";
    private static final AdMapper adMapper = new AdMapper();

    @Test
    public void adToAdDTO() {
        Ad ad = new Ad(Task.builder()
                .usersId(Sets.newHashSet(ID1, ID2, ID3)).build(), URL);
        ad.addProperty(KEY, VALUE);
        AdDTO adDTO = adMapper.adToAdDTO(ad);
        assertEquals(ad.getTask().getUsersId(), adDTO.getUsersId());
        assertEquals(ad.getUrl(), adDTO.getUrl());
        assertEquals(ad.getAdditionalProperties(), adDTO.getAdditionalProperties());
        assertEquals(ad.getTimestamp(), adDTO.getTimestamp());
        assertEquals(ad.getId(), adDTO.getId());
    }

}