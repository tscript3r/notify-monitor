package pl.tscript3r.notify.monitor.containers;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.Statusable;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AdContainer implements Statusable {

    private final Status status = Status.create(this.getClass());
    private final Map<Task, Set<AdDecorated>> tasksAds = new HashMap<>();
    private BigInteger totalReceivedAdsCount = new BigInteger("0");

    public void addAds(Task task, Collection<Ad> ads) {
        addTotalReceivedAdsCount(ads.size());
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task))
                mergeAds(task, AdDecorated.adsToAdDecoratedSizeLimitedSet(ads));
            else
                initialAdAddition(task, ads);
        }
        log.debug("Task id=" + task.getId() + " has " + countNewAds(task) + " new ads");
    }

    private void addTotalReceivedAdsCount(Integer size) {
        totalReceivedAdsCount = totalReceivedAdsCount.add(BigInteger.valueOf(size));
        status.setValue("total_received_ads_count", totalReceivedAdsCount);
    }

    private void mergeAds(Task task, Collection<AdDecorated> ads) {
        tasksAds.get(task).addAll(ads);
    }

    private void initialAdAddition(Task task, Collection<Ad> ads) {
        Set<AdDecorated> adsDecorated =
                AdDecorated.adsToAdDecoratedSizeLimitedSet(getSizeLimit(task, ads.size()), ads);
        adsDecorated.forEach(adDecorated -> adDecorated.returned = true);
        tasksAds.put(task, adsDecorated);
    }

    private int getSizeLimit(Task task, int initialListSize) {
        return Math.round(initialListSize * task.getAdContainerMultiplier());
    }

    public Set<Ad> returnAllAds(Task task) {
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task) && !tasksAds.get(task).isEmpty())
                return AdDecorated.adsDecoratedToAdsSet(tasksAds.get(task));
            else
                return Sets.newHashSet();
        }
    }

    public Set<Ad> returnNewAdsAndMarkAsReturned(Task task) {
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task) && !tasksAds.get(task).isEmpty())
                return AdDecorated.adsDecoratedToAdsSet(tasksAds.get(task)
                        .stream()
                        .filter(adDecorated -> {
                            if (!adDecorated.returned) {
                                adDecorated.returned = true;
                                return true;
                            } else
                                return false;
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            else
                return Sets.newHashSet();

        }
    }

    public Boolean anyAds(Task task) {
        synchronized (tasksAds) {
            return tasksAds.containsKey(task) &&
                    !tasksAds.get(task).isEmpty();
        }
    }

    private Long countNewAds(Task task) {
        return tasksAds.get(task)
                .stream()
                .filter(adDecorated -> !adDecorated.returned)
                .count();
    }

    @Override
    public Status receiveStatus() {
        status.setValue("current_tasks", tasksAds.size());
        status.setValue("current_ads", countTotalStoredAds());
        status.setValue("current_new_ads", countCurrentNewAds());
        status.setValue("total_received_ads", totalReceivedAdsCount);
        return status;
    }

    private Long countTotalStoredAds() {
        long result = 0;
        for (Set<AdDecorated> value : tasksAds.values())
            result += value.size();
        return result;
    }

    private Long countCurrentNewAds() {
        long result = 0;
        for (Task task : tasksAds.keySet())
            result += countNewAds(task);
        return result;
    }

}
