package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long id;

    @NotNull
    @JsonProperty("users_id")
    private Set<Long> usersId;

    @URL
    @NotNull
    private String url;

    @JsonProperty("refresh_interval")
    private Integer refreshInterval;

    @JsonProperty("stored_ads_limit")
    private Integer adContainerLimit;

    @JsonProperty("filters")
    private Set<FilterDTO> filterListDTO;

}
