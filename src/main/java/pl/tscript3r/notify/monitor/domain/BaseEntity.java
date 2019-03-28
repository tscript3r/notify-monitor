package pl.tscript3r.notify.monitor.domain;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    private Long id;

}
