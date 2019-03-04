package pl.tscript3r.notify.monitor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;

@Getter
@Setter
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

}
