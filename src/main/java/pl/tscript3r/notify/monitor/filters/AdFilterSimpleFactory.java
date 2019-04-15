package pl.tscript3r.notify.monitor.filters;

import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.exceptions.AdFilterException;

import java.util.Set;

@Component
public class AdFilterSimpleFactory {

    public AdFilterType getAdFilterType(AdFilter adFilter) {
        if (adFilter instanceof ExcludeAdFilter)
            return AdFilterType.EXCLUDE;
        if (adFilter instanceof IncludeAdFilter)
            return AdFilterType.INCLUDE;
        if (adFilter instanceof MatchAdFilter)
            return AdFilterType.MATCH;
        if (adFilter instanceof RegexAdFilter)
            return AdFilterType.REGEX;
        throw new AdFilterException("Unrecognized adFilter instance");
    }

    public AdFilter getInstance(AdFilterType adFilterType, String propertyKey,
                                Boolean caseSensitive, Set<String> words) {
        switch (adFilterType) {
            case MATCH:
                return new MatchAdFilter(propertyKey, caseSensitive, words);
            case REGEX:
                return new RegexAdFilter(propertyKey, words);
            case EXCLUDE:
                return new ExcludeAdFilter(propertyKey, caseSensitive, words);
            case INCLUDE:
                return new IncludeAdFilter(propertyKey, caseSensitive, words);
            default:
                throw new AdFilterException("Unrecognized adFilter type");
        }
    }

}
