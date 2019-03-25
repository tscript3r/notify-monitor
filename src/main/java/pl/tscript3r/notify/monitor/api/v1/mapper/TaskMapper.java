package pl.tscript3r.notify.monitor.api.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.filters.AdFilter;

import java.util.HashSet;
import java.util.Set;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDTO taskToTaskDTO(Task task);

    default Task taskDTOToTask(TaskDTO taskDTO) {
        if (taskDTO == null)
            return null;
        Task task = new Task();
        task.setId(taskDTO.getId());
        Set<Long> set = taskDTO.getUsersId();
        if (set != null)
            task.setUsersId(new HashSet<Long>(set));
        else
            task.setUsersId(null);
        task.setUrl(taskDTO.getUrl());
        task.setRefreshInterval(taskDTO.getRefreshInterval());
        task.setAdContainerLimit(taskDTO.getAdContainerLimit());
        if (taskDTO.getFilterListDTO() != null &&
                !taskDTO.getFilterListDTO().isEmpty()) {
            FilterMapper filterMapper = FilterMapper.INSTANCE;
            Set<AdFilter> adFilters = new HashSet<>();
            taskDTO.getFilterListDTO().forEach(filterDTO ->
                    adFilters.add(filterMapper.filterDTOToAdFilter(filterDTO)));
            task.setAdFilters(adFilters);
        }
        return task;
    }

}