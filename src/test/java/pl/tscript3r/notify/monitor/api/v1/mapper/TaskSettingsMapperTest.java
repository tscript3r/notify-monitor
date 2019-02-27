package pl.tscript3r.notify.monitor.api.v1.mapper;

import org.junit.Test;
import pl.tscript3r.notify.monitor.api.v1.model.TaskSettingsDTO;
import pl.tscript3r.notify.monitor.domain.TaskSettings;

import static org.junit.Assert.assertEquals;

public class TaskSettingsMapperTest {

    private static final int REFRESH_INTERVAL = 666;
    private TaskSettingsMapper taskSettingsMapper = TaskSettingsMapper.INSTANCE;

    @Test
    public void taskSettingsToTaskSettingsDTO() {
        TaskSettings taskSettings = new TaskSettings(REFRESH_INTERVAL);
        TaskSettingsDTO taskSettingsDTOResult = taskSettingsMapper.taskSettingsToTaskSettingsDTO(taskSettings);

        assertEquals(taskSettings.getRefreshInterval(), taskSettingsDTOResult.getRefreshInterval());
    }
}