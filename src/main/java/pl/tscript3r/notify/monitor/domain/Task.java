package pl.tscript3r.notify.monitor.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private TaskSettings taskSettings;

    private Long lastRefreshTime = 0L;

    // TODO: add enabled property? Can be on "hold" with -> v1/tasks/1/stop | start

    @Builder
    public Task(Long id, Set<Long> usersId, String url, TaskSettings taskSettings) {
        super(id);
        this.usersId = usersId;
        this.url = url;
        this.taskSettings = taskSettings;
    }

    public Boolean refreshable() {
        return (lastRefreshTime + (taskSettings.getRefreshInterval() * 1000)) < System.currentTimeMillis();
    }

    public void setRefreshTime() {
        lastRefreshTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", userId=" + usersId +
                ", url='" + url + '\'' +
                ", taskSettings=" + taskSettings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(usersId, task.usersId) &&
                Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(usersId, getId());
    }
}
