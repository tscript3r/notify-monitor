package pl.tscript3r.notify.monitor.filters;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public class MatchAdFilter extends AbstractAdFilter implements AdFilter {

    public MatchAdFilter(String propertyKey, Boolean caseSensitive, Set<String> matchWords) {
        super(propertyKey, caseSensitive, matchWords);
    }

    @Override
    public Boolean pass(Map<String, String> properties) {
        if (hasPropertyValue(properties)) {
            String source = properties.get(propertyKey);
            if (!caseSensitive)
                source = source.toLowerCase();
            for (String matchWord : words)
                if (compare(source, matchWord))
                    return true;
        }
        return false;
    }

    private Boolean compare(String source, String with) {
        return source.equals(with);
    }

}
