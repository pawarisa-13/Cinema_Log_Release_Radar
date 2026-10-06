package com.cinemalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tmdb")
public record TmdbProperties(String baseUrl, String imageBaseUrl, String apiKey, String language, String region,
        Integer syncPages, String syncCron) {

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    public boolean usesBearerToken() {
        return hasApiKey() && apiKey.length() > 40;
    }

    public String imageUrl(String size, String path) {
        return (path == null || path.isBlank()) ? null : imageBaseUrl + "/" + size + path;
    }
}
