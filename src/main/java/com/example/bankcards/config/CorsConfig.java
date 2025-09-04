package com.example.bankcards.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    // Разрешаем запросы с этих origins
    config.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",    // React dev server
        "https://myfrontend.com"    // Production frontend
    ));

    // Разрешаем методы
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Разрешаем заголовки
    config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

    // Разрешаем credentials (cookies, авторизация)
    config.setAllowCredentials(true);

    // Максимальное время кеширования preflight
    config.setMaxAge(3600L);

    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}