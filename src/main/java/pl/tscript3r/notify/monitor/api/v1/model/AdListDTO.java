package pl.tscript3r.notify.monitor.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdListDTO {

    private List<AdDTO> ads;

}
