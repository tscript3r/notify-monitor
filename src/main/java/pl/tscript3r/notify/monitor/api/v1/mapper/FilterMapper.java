package pl.tscript3r.notify.monitor.api.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.tscript3r.notify.monitor.api.v1.model.FilterDTO;
import pl.tscript3r.notify.monitor.filters.*;

@Mapper
public interface FilterMapper {

    FilterMapper INSTANCE = Mappers.getMapper(FilterMapper.class);

    default AdFilter filterDTOToAdFilter(FilterDTO filterDTO) {
        if (filterDTO.getCaseSensitive() == null)
            filterDTO.setCaseSensitive(false);
        switch (filterDTO.getFilterType()) {
            case MATCH:
                return new MatchAdFilter(filterDTO.getProperty(), filterDTO.getCaseSensitive(), filterDTO.getWords());
            case REGEX:
                return new RegexAdFilter(filterDTO.getProperty(), filterDTO.getWords());
            case EXCLUDE:
                return new ExcludeAdFilter(filterDTO.getProperty(), filterDTO.getCaseSensitive(), filterDTO.getWords());
            case INCLUDE:
                return new IncludeAdFilter(filterDTO.getProperty(), filterDTO.getCaseSensitive(), filterDTO.getWords());
            default:
                throw new RuntimeException();
        }
    }

}
