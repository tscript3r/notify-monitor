package pl.tscript3r.notify.monitor.domain;

import lombok.*;

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
}
