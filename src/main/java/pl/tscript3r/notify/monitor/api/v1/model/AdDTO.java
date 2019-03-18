package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AdDTO {

    @NotNull
    private Long id;

    @NotNull
    @JsonProperty("users_id")
    private Set<Long> usersId;

    @URL
    @NotNull
    private String url;

    @JsonProperty("additional_properties")
    private Map<String, String> additionalProperties;

}
