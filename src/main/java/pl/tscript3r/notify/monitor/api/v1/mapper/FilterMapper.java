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
                return new MatchAdFilter(filterDTO.getProperty(), filterDTO.getCaseSensitive(), filterDTO.getStrings());
            case REGEX:
                return new RegexAdFilter(filterDTO.getProperty(), filterDTO.getStrings());
            case EXCLUDE:
                return new ExcludeAdFilter(filterDTO.getProperty(), filterDTO.getCaseSensitive(), filterDTO.getStrings());
            case INCLUDE:
                return new IncludeAdFilter(filterDTO.getProperty(), filterDTO.getCaseSensitive(), filterDTO.getStrings());
            default:
                throw new RuntimeException();
        }
    }

}
