package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long id;

    @NotEmpty(message = "Users IDs has to be set")
    @JsonProperty("users_id")
    private Set<Long> usersId;

    @URL(message = "Invalid URL")
    @NotEmpty(message = "Observed URL has to be set")
    private String url;

    @JsonProperty("refresh_interval")
    private Integer refreshInterval;

    @JsonProperty("email_send_duration")
    private Integer emailSendDuration;

    @Valid
    @JsonProperty("filters")
    private Set<AdFilterDTO> filterListDTO;

}
