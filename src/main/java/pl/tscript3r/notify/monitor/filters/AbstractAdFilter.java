package pl.tscript3r.notify.monitor.filters;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


abstract class AbstractAdFilter {

    final String propertyKey;
    final Boolean caseSensitive;
    final Set<String> words;

    AbstractAdFilter(String propertyKey, Boolean caseSensitive, Set<String> words) {
        this.propertyKey = propertyKey;
        this.caseSensitive = caseSensitive;
        if (!caseSensitive)
            this.words = toLowerCase(words);
        else
            this.words = words;
    }

    private Set<String> toLowerCase(Set<String> source) {
        return source.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    Boolean hasPropertyValue(Map<String, String> properties) {
        return properties.containsKey(propertyKey) && !properties.get(propertyKey).isEmpty();
    }

}
