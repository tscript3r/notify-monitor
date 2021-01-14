package pl.tscript3r.notify.monitor.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private String email;

    public User(Long id, String email) {
        super(id);
        this.email = email;
    }

}
