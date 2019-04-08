package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AdDTO {

    @NotEmpty
    private Long id;

    @NotEmpty
    @JsonProperty("users_id")
    private Set<Long> usersId;

    @URL
    @NotEmpty
    private String url;

    @JsonProperty("additional_properties")
    private Map<String, String> additionalProperties;

    @Past
    @NotEmpty
    private Timestamp timestamp;

}
