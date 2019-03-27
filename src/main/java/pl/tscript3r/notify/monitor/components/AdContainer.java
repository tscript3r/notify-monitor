package pl.tscript3r.notify.monitor.components;

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
    private final Map<Task, LinkedHashSet<AdDecorated>> tasksAds = new HashMap<>();
    private BigInteger totalReceivedAdsCount = new BigInteger("0");

    private class AdDecorated {
        Ad ad;
        Boolean returned = false;

        AdDecorated(Ad ad) {
            this.ad = ad;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof AdDecorated)) return false;
            AdDecorated that = (AdDecorated) o;
            return Objects.equals(ad, that.ad);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ad);
        }
    }

    private LinkedHashSet<AdDecorated> adsToDecoratedAdsSet(Collection<Ad> ads) {
        return ads.stream()
                .map(AdDecorated::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<Ad> adsDecoratedToAdsSet(Collection<AdDecorated> ads) {
        return ads.stream()
                .map(adDecorated -> adDecorated.ad)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void addAds(Task task, Collection<Ad> ads) {
        addTotalReceivedAdsCount(ads.size());
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task))
                mergeAds(task, adsToDecoratedAdsSet(ads));
            else
                initialAdAddition(task, ads);
            limitAds(task);
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
        LinkedHashSet<AdDecorated> adsDecorated = adsToDecoratedAdsSet(ads);
        adsDecorated.forEach(adDecorated -> adDecorated.returned = true);
        tasksAds.put(task, adsDecorated);
    }

    // TODO: probably to refactor
    private void limitAds(Task task) {
        if (tasksAds.get(task).size() > task.getAdContainerLimit()) {
            LinkedHashSet<AdDecorated> currentSet = tasksAds.get(task);
            LinkedHashSet<AdDecorated> cutOffSet = new LinkedHashSet<>(task.getAdContainerLimit());
            Iterator it = currentSet.iterator();
            int removeCount = tasksAds.get(task).size() - task.getAdContainerLimit();
            int skippedCount = 0;
            do {
                AdDecorated adDecorated = (AdDecorated) it.next();
                if (skippedCount >= removeCount)
                    cutOffSet.add(adDecorated);
                skippedCount++;
            } while (it.hasNext());
            tasksAds.put(task, cutOffSet);
            log.debug("Task id=" + task.getId() + " stored ads list has been limited (size=" + cutOffSet.size() + ")");
        }
    }

    public Set<Ad> returnAllAds(Task task) {
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task) && !tasksAds.get(task).isEmpty())
                return adsDecoratedToAdsSet(tasksAds.get(task));
            else
                return Sets.newHashSet();
        }
    }

    public Set<Ad> returnNewAdsAndMarkAsReturned(Task task) {
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task) && !tasksAds.get(task).isEmpty())
                return adsDecoratedToAdsSet(tasksAds.get(task)
                        .stream()
                        .filter(adDecorated -> {
                            if (!adDecorated.returned) {
                                adDecorated.returned = true;
                                return true;
                            } else
                                return false;
                        })
                        .collect(Collectors.toCollection(ArrayList::new)));
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
        for (LinkedHashSet<AdDecorated> value : tasksAds.values())
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
