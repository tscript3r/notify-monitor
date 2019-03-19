package pl.tscript3r.notify.monitor.status;

import java.util.HashMap;
import java.util.Map;

public class Status {

    private final Class ownerClass;
    private Map<String, String> values = new HashMap<>(5);

    public static Status create(Class ownerClass) {
        return new Status(ownerClass);
    }

    private Status(Class ownerClass) {
        this.ownerClass = ownerClass;
    }

    public void addValue(String key, String value) {
        values.put(key, value);
    }

    public String getName() {
        return ownerClass.getSimpleName();
    }

    public Map<String, String> getValues() {
        return values;
    }

}
