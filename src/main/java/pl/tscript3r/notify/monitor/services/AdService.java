package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.status.Statusable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AdService extends Statusable {

    List<AdDTO> getNewAds(Task task);

    Map<Task, Set<AdDTO>> getAllNewAds();

}
