package pl.tscript3r.notify.monitor.components;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;

import static org.junit.Assert.assertEquals;

public class TaskDefaultValueSetterTest {

    TaskDefaultValueSetter taskDefaultValueSetter;

    @Before
    public void setUp() throws Exception {
        taskDefaultValueSetter = new TaskDefaultValueSetter(120, 60, 60, 30);
    }

    @Test
    public void validateAndSetDefaultsNulls() {
        TaskDTO taskDTO = new TaskDTO(1L, Sets.newHashSet(1L), "https://www.olx.pl/", null, null);
        taskDefaultValueSetter.validateAndSetDefaults(taskDTO);
        assertEquals(120, taskDTO.getRefreshInterval().intValue());
        assertEquals(60, taskDTO.getAdContainerLimit().intValue());
    }

    @Test
    public void validateAndSetDefaultsToLowValues() {
        TaskDTO taskDTO = new TaskDTO(1L, Sets.newHashSet(1L), "https://www.olx.pl/", 59, 29);
        taskDefaultValueSetter.validateAndSetDefaults(taskDTO);
        assertEquals(120, taskDTO.getRefreshInterval().intValue());
        assertEquals(60, taskDTO.getAdContainerLimit().intValue());
    }

}