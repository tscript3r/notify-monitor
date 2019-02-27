package pl.tscript3r.notify.monitor.api.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.tscript3r.notify.monitor.api.v1.model.TaskSettingsDTO;
import pl.tscript3r.notify.monitor.domain.TaskSettings;

@Mapper
public interface TaskSettingsMapper {
    TaskSettingsMapper INSTANCE = Mappers.getMapper(TaskSettingsMapper.class);

    TaskSettingsDTO taskSettingsToTaskSettingsDTO(TaskSettings task);

    TaskSettings taskSettingsDTOToTaskSettings(TaskSettingsDTO task);
}
