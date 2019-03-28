package pl.tscript3r.notify.monitor.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode(of = "url", callSuper = false)
public class Ad extends BaseEntity {

    @Getter
    @NotNull
    private final Task task;

    @Getter
    @URL
    @NotNull
    private final String url;

    @Getter
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    private static long idCounter = 0L;

    @Getter
    private Map<String, String> additionalProperties = new HashMap<>();

    private static long getIdValue() {
        return idCounter++;
    }

    public Ad(Task task, String url) {
        super(getIdValue());
        this.task = task;
        this.url = url;
    }

    public void addProperty(String key, String value) {
        if (value != null && key != null &&
                !key.isEmpty() && !value.isEmpty())
            additionalProperties.put(key, value);
    }

    public Boolean hasKey(String key) {
        return additionalProperties.containsKey(key) && !additionalProperties.get(key).isEmpty();
    }

    public String getValue(String key) {
        if (hasKey(key))
            return additionalProperties.get(key);
        else
            return "";
    }

}
