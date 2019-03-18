package pl.tscript3r.notify.monitor.domain;

import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Ad extends BaseEntity {

    private static Long idCounter = 0L;

    @Getter
    @NotNull
    private final Task task;

    @Getter
    @URL
    @NotNull
    private final String url;

    @Getter
    private Map<String, String> additionalProperties = new HashMap<>();

    @Getter
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public Ad(Task task, String url) {
        setId(idCounter++);
        this.task = task;
        this.url = url;
    }

    public void addProperty(String key, String value) {
        if (value != null && !value.isEmpty())
            additionalProperties.put(key, value);
    }

    public Boolean hasValue(String key) {
        return additionalProperties.containsKey(key) && !additionalProperties.get(key).isEmpty();
    }

    public String getValue(String key) {
        if (hasValue(key))
            return additionalProperties.get(key);
        else
            return "";
    }

    @Override
    public String toString() {
        return "Ad{" +
                "task.id=" + task.getId() +
                ", url='" + url + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return Objects.equals(url, ad.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

}
