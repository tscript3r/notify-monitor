package pl.tscript3r.notify.monitor.api.v1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FilterListDTO {

    private Set<FilterDTO> filters;

}
