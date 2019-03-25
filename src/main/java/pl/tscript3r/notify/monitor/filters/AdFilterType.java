package pl.tscript3r.notify.monitor.filters;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AdFilterType {

    @JsonProperty("exclude")
    EXCLUDE,
    @JsonProperty("include")
    INCLUDE,
    @JsonProperty("regex")
    REGEX,
    @JsonProperty("match")
    MATCH

}
