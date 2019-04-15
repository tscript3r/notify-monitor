package pl.tscript3r.notify.monitor.api.v1.mapper;

import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.api.v1.model.AdFilterDTO;
import pl.tscript3r.notify.monitor.exceptions.AdFilterException;
import pl.tscript3r.notify.monitor.filters.*;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class AdFilterMapper {

    private final AdFilterSimpleFactory adFilterSimpleFactory;

    public AdFilterMapper(AdFilterSimpleFactory adFilterSimpleFactory) {
        this.adFilterSimpleFactory = adFilterSimpleFactory;
    }

    public AdFilter adFilterDTOToAdFilter(AdFilterDTO adFilterDTO) {
        if (adFilterDTO.getCaseSensitive() == null)
            adFilterDTO.setCaseSensitive(false);
        return adFilterSimpleFactory.getInstance(adFilterDTO.getFilterType(),
                adFilterDTO.getProperty(), adFilterDTO.getCaseSensitive(), adFilterDTO.getStrings());
    }

    public AdFilterDTO adFilterToAdFilterDTO(AdFilter adFilter) {
        final String UNRECOGNIZED_AD_FILTER_TYPE = "Unrecognized adFilter type";
        if (adFilter instanceof ExcludeAdFilter || adFilter instanceof IncludeAdFilter ||
                adFilter instanceof MatchAdFilter) {
            AbstractAdFilter abstractAdFilter = (AbstractAdFilter) adFilter;
            AdFilterDTO adFilterDTO = new AdFilterDTO();
            adFilterDTO.setCaseSensitive(abstractAdFilter.getCaseSensitive());
            adFilterDTO.setProperty(abstractAdFilter.getPropertyKey());
            adFilterDTO.setStrings(abstractAdFilter.getWords());
            adFilterDTO.setFilterType(adFilterSimpleFactory.getAdFilterType(adFilter));
            return adFilterDTO;
        } else if (adFilter instanceof RegexAdFilter) {
            RegexAdFilter regexAdFilter = (RegexAdFilter) adFilter;
            AdFilterDTO adFilterDTO = new AdFilterDTO();
            adFilterDTO.setFilterType(adFilterSimpleFactory.getAdFilterType(adFilter));
            adFilterDTO.setStrings(regexAdFilter.getPatterns()
                    .stream()
                    .map(Pattern::toString)
                    .collect(Collectors.toSet()));
            adFilterDTO.setCaseSensitive(false);
            adFilterDTO.setProperty(regexAdFilter.getPropertyKey());
            return adFilterDTO;
        } else
            throw new AdFilterException(UNRECOGNIZED_AD_FILTER_TYPE);
    }

}
