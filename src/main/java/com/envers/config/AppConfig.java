package com.envers.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author abidk
 *
 */
@Configuration
@ComponentScan(basePackages = "com.envers")
@Import({ PropertySourcesPlaceholderConfiguration.class })
public class AppConfig {

}
