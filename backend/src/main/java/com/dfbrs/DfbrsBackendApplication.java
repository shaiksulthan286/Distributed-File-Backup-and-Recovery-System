package com.dfbrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.boot.context.properties.EnableConfigurationProperties({
        com.dfbrs.config.StorageProperties.class,
        com.dfbrs.config.JwtProperties.class
})
public class DfbrsBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(DfbrsBackendApplication.class, args);
    }
}
