package pl.tscript3r.notify.monitor.controllers.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tscript3r.notify.monitor.api.v1.model.AdListDTO;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.services.AdService;
import pl.tscript3r.notify.monitor.services.TaskService;
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.Statusable;

@Slf4j
@RestController
@RequestMapping(Paths.AD_TASK_PATH)
public class AdController implements Statusable {

    public static final String NEW_ADS_RETURN_CALLS = "new_ads_return_calls";

    private final Status status = Status.create(this.getClass());
    private final TaskService taskService;
    private final AdService adService;

    public AdController(TaskService taskService, AdService adService) {
        this.taskService = taskService;
        this.adService = adService;
        status.initIntegerCounterValues(NEW_ADS_RETURN_CALLS);
    }

    @GetMapping
    public AdListDTO getNewAds(@PathVariable Long id) {
        status.incrementValue(NEW_ADS_RETURN_CALLS);
        return new AdListDTO(adService.getNewAds(taskService.getTaskById(id)));
    }

    @Override
    public Status receiveStatus() {
        return status;
    }
}
