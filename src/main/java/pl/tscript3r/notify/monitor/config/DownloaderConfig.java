package pl.tscript3r.notify.monitor.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties("notify.monitor.downloader")
public class DownloaderConfig {

    private Integer connectionTimeout;
    private Integer maxBodySize;
    private String userAgent;
    private Boolean followRedirects;

}