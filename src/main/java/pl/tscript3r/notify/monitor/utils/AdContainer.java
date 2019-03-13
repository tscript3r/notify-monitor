package pl.tscript3r.notify.monitor.utils;

import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class AdContainer {

    private final Map<Task, Set<Ad>> tasksAds = new LinkedHashMap<>();

    public void addAds(Task task, Set<Ad> ads) {
        if( !tasksAds.containsKey(task))
            tasksAds.put(task, ads);
        else
            mergeAds(task, ads);
    }

    public Set<Ad> retreiveAds(Task task) {
        return tasksAds.get(task);
    }

    public Boolean anyAds(Task task) {
        return tasksAds.containsKey(task) &&
                !tasksAds.get(task).isEmpty();
    }

    private void mergeAds(Task task, Set<Ad> ads) {
        tasksAds.get(task).addAll(ads);
    }

}
