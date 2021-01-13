package pl.tscript3r.notify.monitor.api.v1.mapper;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.domain.Ad;

@Component
public class AdMapper {

    private final ModelMapper mapper = new ModelMapper();

    public AdDTO adToAdDTO(Ad ad) {
        return mapper.map(ad, AdDTO.class);
    }

}
