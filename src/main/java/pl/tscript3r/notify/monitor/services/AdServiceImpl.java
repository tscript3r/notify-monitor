package pl.tscript3r.notify.monitor.services;

import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.api.v1.mapper.AdMapper;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.components.AdContainer;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    private final TaskService taskService;
    private final AdContainer adContainer;
    private final AdMapper adMapper;

    public AdServiceImpl(TaskService taskService, AdContainer adContainer, AdMapper adMapper) {
        this.taskService = taskService;
        this.adContainer = adContainer;
        this.adMapper = adMapper;
    }

    @Override
    public List<AdDTO> getNewAds(Task task) {
        if (task == null)
            throw new TaskNotFoundException("Empty value");
        if (taskService.isAdded(task))
            return adContainer.returnNewAdsAndMarkAsReturned(task)
                    .stream()
                    .map(adMapper::adToAdDTO)
                    .collect(Collectors.toList());
        else
            throw new TaskNotFoundException(task.getId());
    }

}
