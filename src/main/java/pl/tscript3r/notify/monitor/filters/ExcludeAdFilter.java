package pl.tscript3r.notify.monitor.filters;

import java.util.Map;
import java.util.Set;

public class ExcludeAdFilter extends AbstractAdFilter implements AdFilter {

    public ExcludeAdFilter(String propertyKey, Boolean caseSensitive, Set<String> excludedWords) {
        super(propertyKey, caseSensitive, excludedWords);
    }

    @Override
    public Boolean pass(Map<String, String> properties) {
        if (hasPropertyValue(properties)) {
            String source = properties.get(propertyKey);
            if (!caseSensitive)
                source = source.toLowerCase();
            for (String excludedWord : words)
                if (includesWord(source, excludedWord))
                    return false;
        }
        return true;
    }

    private Boolean includesWord(String source, String word) {
        return source.contains(word);
    }

}