package pl.tscript3r.notify.monitor.filters;

import java.util.Map;
import java.util.Set;

public class IncludeAdFilter extends AbstractAdFilter implements AdFilter {

    public IncludeAdFilter(String propertyKey, Boolean caseSensitive, Set<String> includeWords) {
        super(propertyKey, caseSensitive, includeWords);
    }

    @Override
    public Boolean pass(Map<String, String> properties) {
        if (hasPropertyValue(properties)) {
            String source = properties.get(propertyKey);
            if (!caseSensitive)
                source = source.toLowerCase();
            for (String excludedWord : words)
                if (includesWord(source, excludedWord))
                    return true;
        }
        return false;
    }

    private Boolean includesWord(String source, String word) {
        return source.contains(word);
    }

}
