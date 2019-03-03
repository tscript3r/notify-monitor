package pl.tscript3r.notify.monitor.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Task extends BaseEntity {

    private Set<Long> usersId;
    private String url;
    private TaskSettings taskSettings;

    @Builder
    public Task(Long id, Set<Long> usersId, String url, TaskSettings taskSettings) {
        super(id);
        this.usersId = usersId;
        this.url = url;
        this.taskSettings = taskSettings;
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
