package pl.tscript3r.notify.monitor.api.v1.mapper;

import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.AdFilterDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.filters.AdFilter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class TaskMapper {

    private final AdFilterMapper adFilterMapper;

    public TaskMapper(AdFilterMapper adFilterMapper) {
        this.adFilterMapper = adFilterMapper;
    }

    public TaskDTO taskToTaskDTO(Task task) {
        if (task == null)
            return null;
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        Set<Long> set = task.getUsersId();
        if (set != null)
            taskDTO.setUsersId(new HashSet<Long>(set));
        else
            taskDTO.setUsersId(null);
        taskDTO.setUrl(task.getUrl());
        taskDTO.setRefreshInterval(task.getRefreshInterval());
        if (task.getAdFilters() != null &&
                !task.getAdFilters().isEmpty()) {
            Set<AdFilterDTO> adFiltersDTO = new HashSet<>();
            task.getAdFilters().forEach(adFilter ->
                    adFiltersDTO.add(adFilterMapper.adFilterToAdFilterDTO(adFilter)));
            taskDTO.setFilterListDTO(adFiltersDTO);
        } else
            taskDTO.setFilterListDTO(Collections.emptySet());
        return taskDTO;
    }

    public Task taskDTOToTask(TaskDTO taskDTO) {
        if (taskDTO == null)
            return null;
        Task task = new Task();
        task.setId(taskDTO.getId());
        Set<Long> set = taskDTO.getUsersId();
        if (set != null)
            task.setUsersId(new HashSet<>(set));
        else
            task.setUsersId(null);
        task.setUrl(taskDTO.getUrl());
        task.setRefreshInterval(taskDTO.getRefreshInterval());
        if (taskDTO.getFilterListDTO() != null &&
                !taskDTO.getFilterListDTO().isEmpty()) {
            Set<AdFilter> adFilters = new HashSet<>();
            taskDTO.getFilterListDTO().forEach(filterDTO ->
                    adFilters.add(adFilterMapper.adFilterDTOToAdFilter(filterDTO)));
            task.setAdFilters(adFilters);
        }
        return task;
    }

}