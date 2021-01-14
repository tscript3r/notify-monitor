package pl.tscript3r.notify.monitor.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MapperConfig {

    @Bean
    ModelMapper getModelMapper() {
        return new ModelMapper();
    }

}
