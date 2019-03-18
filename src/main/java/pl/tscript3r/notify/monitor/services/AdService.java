package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.List;

public interface AdService {

    List<AdDTO> getNewAds(Task task);

}
