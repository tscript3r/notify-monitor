package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TaskDTO {

    private Long id;

    @NotNull
    @JsonProperty("user_id")
    private Long userId;

    @URL
    @NotNull
    private String url;

    @JsonProperty("task_settings")
    private TaskSettingsDTO taskSettings;

}
