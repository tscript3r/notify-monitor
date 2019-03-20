package pl.tscript3r.notify.monitor.status;

import java.util.HashMap;
import java.util.Map;

public class Status {

    private final Class ownerClass;
    private Map<String, Object> values = new HashMap<>(5);

    public static Status create(Class ownerClass) {
        return new Status(ownerClass);
    }

    private Status(Class ownerClass) {
        this.ownerClass = ownerClass;
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    public void initIntegerCounterValues(String... values) {
        for (String value : values)
            setValue(value, 0);
    }

    public void incrementValue(String key, Integer count) {
        if (values.containsKey(key)) {
            Object value = values.get(key);
            if (isNumeric(value))
                value = tryIncrement(value, count);
            else
                value = count;
            values.put(key, value);
        }
    }

    private Boolean isNumeric(Object o) {
        return o instanceof Number;
    }

    private Object tryIncrement(Object value, Integer count) {
        try {
            Integer convertedValue = Integer.valueOf(value.toString());
            return convertedValue + count;
        } catch (NumberFormatException | ClassCastException e) {
            return count;
        }
    }

    public void incrementValue(String key) {
        incrementValue(key, 1);
    }

    public String getName() {
        return ownerClass.getSimpleName();
    }

    public Map<String, Object> getProperties() {
        return values;
    }

}
