package org.exercise.inventorymanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private Map<String, Set<String>> allowedItems;

}
