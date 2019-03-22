package pl.tscript3r.notify.monitor.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FilterTypeDTO {

    @JsonProperty("exclude")
    EXCLUDE,
    @JsonProperty("include")
    INCLUDE,
    @JsonProperty("regex")
    REGEX,
    @JsonProperty("match")
    MATCH

}
