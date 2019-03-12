package pl.tscript3r.notify.monitor.domain;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = "task")
@NoArgsConstructor
@AllArgsConstructor
public class Ad extends BaseEntity {

    @NotNull
    private Task task;

    @URL
    private String url;
    private String thumbnailUrl;
    private String price;
    private String category;

    @NotNull
    private String title;

    @NotNull
    private String location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return Objects.equals(url, ad.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
