package pl.tscript3r.notify.monitor.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@AllArgsConstructor
public class FilterListDTO {

    private Set<FilterDTO> filters;

}
