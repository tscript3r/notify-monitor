package pl.tscript3r.notify.monitor.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.tscript3r.notify.monitor.filters.AdFilter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
public class Task extends BaseEntity {

    @Getter
    @Setter
    private Set<Long> usersId;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private Integer refreshInterval;

    @Getter
    @Setter
    private Integer adContainerLimit;

    @Getter
    @Setter
    private Set<AdFilter> adFilters = new HashSet<>();

    private Long lastRefreshTime = 0L;

    @Builder
    public Task(Long id, Set<Long> usersId, String url, Integer refreshInterval, Integer adContainerLimit) {
        super(id);
        this.usersId = usersId;
        this.url = url;
        this.refreshInterval = refreshInterval;
        this.adContainerLimit = adContainerLimit;
    }

    public Boolean isRefreshable() {
        synchronized (lastRefreshTime) {
            return (lastRefreshTime + (refreshInterval * 1000)) < System.currentTimeMillis();
        }
    }

    public void setRefreshTime() {
        synchronized (lastRefreshTime) {
            lastRefreshTime = System.currentTimeMillis();
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", userId=" + usersId +
                ", url='" + url + '\'' +
                ", refreshInterval=" + refreshInterval +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
