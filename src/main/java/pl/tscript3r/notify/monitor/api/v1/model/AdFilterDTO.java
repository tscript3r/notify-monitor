package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.tscript3r.notify.monitor.filters.AdFilterType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdFilterDTO {

    @NotNull(message = "Filter type has to be set, available filter types are: regex, match, include, exclude")
    @JsonProperty("filter_type")
    private AdFilterType filterType;

    @NotEmpty(message = "Property value has to be set")
    private String property;

    @JsonProperty("case_sensitive")
    private Boolean caseSensitive;

    @NotEmpty(message = "Filter strings has to be set")
    private Set<String> strings;

}
