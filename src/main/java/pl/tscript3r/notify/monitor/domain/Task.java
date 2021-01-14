package pl.tscript3r.notify.monitor.domain;

import lombok.*;
import pl.tscript3r.notify.monitor.filters.AdFilter;

import java.util.HashSet;
import java.util.Set;

@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
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
    private Float adContainerMultiplier;

    @Getter
    @Setter
    private Set<AdFilter> adFilters = new HashSet<>();

    private Long lastRefreshTime = 0L;

    @Builder
    public Task(Long id, Set<Long> usersId, String url, Integer refreshInterval, Float adContainerMultiplier) {
        super(id);
        this.usersId = usersId;
        this.url = url;
        this.refreshInterval = refreshInterval;
        this.adContainerMultiplier = adContainerMultiplier;
    }

    public Boolean isRefreshable() {
        return (lastRefreshTime + (refreshInterval * 1000)) < System.currentTimeMillis();
    }

    public void setRefreshTime() {
        lastRefreshTime = System.currentTimeMillis();
    }

}
