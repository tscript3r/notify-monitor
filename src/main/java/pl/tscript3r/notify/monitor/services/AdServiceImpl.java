package pl.tscript3r.notify.monitor.services;

import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.api.v1.mapper.AdMapper;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.containers.AdContainer;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;
import pl.tscript3r.notify.monitor.status.Status;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    private static final String RETURNED_NEW_ADS = "returned_new_ads";

    private final Status status = Status.create(this.getClass());
    private final TaskService taskService;
    private final AdContainer adContainer;
    private final AdMapper adMapper;

    public AdServiceImpl(TaskService taskService, AdContainer adContainer, AdMapper adMapper) {
        this.taskService = taskService;
        this.adContainer = adContainer;
        this.adMapper = adMapper;
        status.initIntegerCounterValues(RETURNED_NEW_ADS);
    }

    @Override
    public List<AdDTO> getNewAds(Task task) {
        status.incrementValue(RETURNED_NEW_ADS);
        if (task == null)
            throw new TaskNotFoundException("null value");
        if (taskService.isAdded(task))
            return adContainer.returnNewAds(task)
                    .stream()
                    .map(adMapper::adToAdDTO)
                    .collect(Collectors.toList());
        else
            throw new TaskNotFoundException(task.getId());
    }

    @Override
    public Map<Task, Set<AdDTO>> getAllNewAds() {
        return adContainer.getNewAds()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> adMapper.adToAdDTO(e.getValue())));
    }

    @Override
    public Boolean isFull(Task task) {
        return adContainer.isFull(task);
    }

    @Override
    public void markAsSend(Task task) {
        adContainer.markAllAsReturned(task);
    }

    @Override
    public Status receiveStatus() {
        return status;
    }
}
