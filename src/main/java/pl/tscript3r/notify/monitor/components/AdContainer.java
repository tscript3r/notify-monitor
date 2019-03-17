package pl.tscript3r.notify.monitor.components;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AdContainer {

    private class AdDecorated {
        Ad ad;
        Boolean returned = false;

        AdDecorated(Ad ad) {
            this.ad = ad;
        }

        @Override
        public boolean equals(Object o) {
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

    private final Map<Task, LinkedHashSet<AdDecorated>> tasksAds = new HashMap<>();

    /**
     * Adds ads to the given <b>task</b>, if any of the given ad is
     * duplicated will be skipped
     *
     * @param ads Any collection
     */
    public void addAds(Task task, Collection<Ad> ads) {
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task))
                mergeAds(task, adsToDecoratedAdsSet(ads));
            else
                initialAdAddition(task, ads);

            limitAds(task);
        }
        log.debug("Task id=" + task.getId() + " has " + countNewAds(task) + " new ads");
    }

    private void mergeAds(Task task, Collection<AdDecorated> ads) {
        tasksAds.get(task).addAll(ads);
    }

    private void initialAdAddition(Task task, Collection<Ad> ads) {
        LinkedHashSet<AdDecorated> adsDecorated = adsToDecoratedAdsSet(ads);
        adsDecorated.forEach(adDecorated -> adDecorated.returned = true);
        tasksAds.put(task, adsDecorated);
    }

    // TODO: probably to refactor, add test
    private void limitAds(Task task) {
        if (tasksAds.get(task).size() > task.getAdContainerLimit()) {
            LinkedHashSet<AdDecorated> currentSet = tasksAds.get(task);
            LinkedHashSet<AdDecorated> cutOffSet = new LinkedHashSet<>(task.getAdContainerLimit());
            Iterator it = currentSet.iterator();
            int removeCount = tasksAds.get(task).size() - task.getAdContainerLimit();
            int skippedCount = 0;
            while (it.hasNext()) {
                AdDecorated adDecorated = (AdDecorated) it.next();
                if (skippedCount >= removeCount)
                    cutOffSet.add(adDecorated);
                skippedCount++;
            }
            tasksAds.put(task, cutOffSet);
            log.debug("Task id=" + task.getId() + " stored ads list has been limited (size=" + cutOffSet.size() + ")");
        }
    }

    /**
     * @param task
     * @return Returns all currently stored ads for the given task
     */
    public Set<Ad> returnAllAds(Task task) {
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task) && tasksAds.get(task).size() > 0)
                return adsDecoratedToAdsSet(tasksAds.get(task));
            else
                return Sets.newHashSet();
        }
    }

    /**
     * @param task
     * @return Returns only the ads which has been not yet returned, and marks them as returned
     */
    public Set<Ad> returnNewAdsAndMarkAsReturned(Task task) {
        synchronized (tasksAds) {
            if (tasksAds.containsKey(task) && tasksAds.get(task).size() > 0)
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

    /**
     * @param task
     * @return <b>true</b> when the given task has been already added and contains any ads
     */
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
}
