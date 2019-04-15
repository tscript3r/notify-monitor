package pl.tscript3r.notify.monitor.filters;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public class RegexAdFilter implements AdFilter {

    private final String propertyKey;
    private final Set<Pattern> patterns;

    public RegexAdFilter(String propertyKey, Set<String> patterns) {
        this.propertyKey = propertyKey;
        this.patterns = patterns.stream()
                .map(Pattern::compile)
                .collect(Collectors.toSet());
    }

    @Override
    public Boolean pass(Map<String, String> properties) {
        if (hasPropertyValue(properties))
            return compare(properties.get(propertyKey));
        else
            return false;
    }

    private Boolean hasPropertyValue(Map<String, String> properties) {
        return properties.containsKey(propertyKey) && !properties.get(propertyKey).isEmpty();
    }

    private Boolean compare(String source) {
        for (Pattern pattern : patterns)
            if (pattern.matcher(source).find())
                return true;
        return false;
    }

}
