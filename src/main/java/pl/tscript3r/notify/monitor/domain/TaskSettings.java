package pl.tscript3r.notify.monitor.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSettings {

    // predicting that something more will apear here in the future
    private Integer refreshInterval;

}
