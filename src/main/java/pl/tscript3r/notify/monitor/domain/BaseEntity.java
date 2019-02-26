package pl.tscript3r.notify.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    private Long id;

    public Boolean isNew() {
        return this.id == null;
    }

}
