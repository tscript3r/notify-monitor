package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.List;

public interface AdService {

    List<Ad> getCurrentAds(Task task);

}
