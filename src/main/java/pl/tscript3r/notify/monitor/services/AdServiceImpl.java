package pl.tscript3r.notify.monitor.services;

import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.components.AdContainer;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.Set;

@Service
public class AdServiceImpl implements AdService {

    private final AdContainer adContainer;

    public AdServiceImpl(AdContainer adContainer) {
        this.adContainer = adContainer;
    }

    @Override
    public Set<Ad> getCurrentAds(Task task) {
        return adContainer.returnAllAds(task);
    }

}
