package pl.tscript3r.notify.monitor.services;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.filters.AdFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdFilterServiceImpl implements AdFilterService {

    private final Map<Task, Set<AdFilter>> taskAdFilters = new HashMap<>();

    @Override
    public void add(Task task, AdFilter adFilter) {
        synchronized (taskAdFilters) {
            if (taskAdFilters.containsKey(task))
                taskAdFilters.get(task).add(adFilter);
            else
                taskAdFilters.put(task, Sets.newHashSet(adFilter));
        }
    }

    @Override
    public void remove(Task task) {
        synchronized (taskAdFilters) {
            taskAdFilters.remove(task);
        }
    }

    @Override
    public List<Ad> filter(List<Ad> ads) {
        if (!ads.isEmpty() && taskAdFilters.containsKey(getTask(ads))) {
            synchronized (taskAdFilters) {
                taskAdFilters.get(getTask(ads)).forEach(adFilter ->
                        ads.removeIf(ad -> !adFilter.pass(ad.getAdditionalProperties())));
            }
        }
        return ads;
    }

    private Task getTask(List<Ad> ads) {
        return ads.get(0).getTask();
    }

}
