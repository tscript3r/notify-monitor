package pl.tscript3r.notify.monitor.services;

import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.List;

@Service
public class AdServiceImpl implements AdService {

    @Override
    public List<Ad> getCurrentAds(Task task) {
        return null;
    }
    
}
