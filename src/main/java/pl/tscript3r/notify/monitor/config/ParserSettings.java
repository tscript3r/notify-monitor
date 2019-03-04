package pl.tscript3r.notify.monitor.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties("notify.monitor.parser")
public class ParserSettings {

    private Integer parserThreadCapacity;
    private Integer defaultInterval;
    private Integer connectionTimeout;
    private Integer maxBodySize;
    private String userAgent;
    private Boolean followRedirects;

}
