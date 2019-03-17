package pl.tscript3r.notify.monitor.services;

import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.components.AdContainer;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;

import java.util.Set;

@Service
public class AdServiceImpl implements AdService {

    private final TaskService taskService;
    private final AdContainer adContainer;

    public AdServiceImpl(TaskService taskService, AdContainer adContainer) {
        this.taskService = taskService;
        this.adContainer = adContainer;
    }

    @Override
    public Set<Ad> getCurrentAds(Task task) {
        if (task == null)
            throw new TaskNotFoundException("Empty value");
        if (taskService.isAdded(task))
            return adContainer.returnNewAdsAndMarkAsReturned(task);
        else
            throw new TaskNotFoundException(task.getId());
    }

}
