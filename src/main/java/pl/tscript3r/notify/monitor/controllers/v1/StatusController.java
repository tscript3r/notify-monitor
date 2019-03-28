package pl.tscript3r.notify.monitor.controllers.v1;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.StatusCollector;

import java.util.List;

@Api("Retrieves current status of the application components")
@RestController
@RequestMapping(Paths.STATUS_PATH)
public class StatusController {

    private final StatusCollector statusCollector;

    public StatusController(StatusCollector statusCollector) {
        this.statusCollector = statusCollector;
    }

    @GetMapping
    public List<Status> getTotalStatus() {
        return statusCollector.getAll();
    }

}
