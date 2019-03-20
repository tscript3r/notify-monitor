package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.status.Statusable;

import java.util.List;

public interface AdService extends Statusable {

    List<AdDTO> getNewAds(Task task);

}
