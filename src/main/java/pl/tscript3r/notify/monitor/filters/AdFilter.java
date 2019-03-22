package pl.tscript3r.notify.monitor.filters;

import java.util.Map;

public interface AdFilter {

    Boolean pass(Map<String, String> properties);

}
