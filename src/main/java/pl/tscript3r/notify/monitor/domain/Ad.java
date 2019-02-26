package pl.tscript3r.notify.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ad extends BaseEntity {

    private Long userId;
    private Long taskId;
    private String url;
    private String thumbnailUrl;

}
