package pl.tscript3r.notify.monitor.domain;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Task extends BaseEntity {

    private Long userId;
    private String url;
    private TaskSettings taskSettings;

    @Builder
    public Task(Long id, Long userId, String url, TaskSettings taskSettings) {
        super(id);
        this.userId = userId;
        this.url = url;
        this.taskSettings = taskSettings;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", userId=" + userId +
                ", url='" + url + '\'' +
                ", taskSettings=" + taskSettings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(userId, task.userId) &&
                Objects.equals(url, task.url) &&
                 Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, url, getId());
    }

}
