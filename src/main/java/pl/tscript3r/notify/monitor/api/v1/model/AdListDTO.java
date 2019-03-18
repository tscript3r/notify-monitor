package pl.tscript3r.notify.monitor.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdListDTO {

    private List<AdDTO> ads;

}
