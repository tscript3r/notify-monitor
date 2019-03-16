package pl.tscript3r.notify.monitor.components;

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

    private HashSet<AdDecorated> adsToDecoratedAdsSet(Collection<Ad> ads) {
        return ads.stream()
                .map(AdDecorated::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private HashSet<Ad> adsDecoratedToAdsSet(Collection<AdDecorated> ads) {
        return ads.stream()
                .map(adDecorated -> adDecorated.ad)
                .collect(Collectors.toCollection(HashSet::new));
    }

    // TODO: limit HashSet
    private final Map<Task, HashSet<AdDecorated>> tasksAds = new HashMap<>();

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
        }
        log.debug("Task id=" + task.getId() + " has " + countNewAds(task) + " new ads");
    }

    private void mergeAds(Task task, Collection<AdDecorated> ads) {
        tasksAds.get(task).addAll(ads);
    }

    private void initialAdAddition(Task task, Collection<Ad> ads) {
        Collection<AdDecorated> adsDecorated = adsToDecoratedAdsSet(ads);
        adsDecorated.forEach(adDecorated -> adDecorated.returned = true);
        tasksAds.put(task, adsToDecoratedAdsSet(ads));
    }

    /**
     * @param task
     * @return Returns all currently stored ads for the given task
     */
    public Set<Ad> returnAllAds(Task task) {
        synchronized (tasksAds) {
            return adsDecoratedToAdsSet(tasksAds.get(task));
        }
    }

    /**
     * @param task
     * @return Returns only the ads which has been not yet returned, and marks them as returned
     */
    public Set<Ad> returnNewAdsAndMarkAsReturned(Task task) {
        synchronized (tasksAds) {
            return adsDecoratedToAdsSet(tasksAds.get(task)
                    .stream()
                    .filter(adDecorated -> {
                        if (!adDecorated.returned) {
                            adDecorated.returned = true;
                            return true;
                        } else
                            return false;
                    })
                    .collect(Collectors.toCollection(HashSet::new)));
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
