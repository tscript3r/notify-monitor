package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.tscript3r.notify.monitor.filters.AdFilterType;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {

    @NotNull
    @JsonProperty("filter_type")
    private AdFilterType filterType;

    @NotNull
    private String property;

    @JsonProperty("case_sensitive")
    private Boolean caseSensitive;

    @NotNull
    private Set<String> words;

}
