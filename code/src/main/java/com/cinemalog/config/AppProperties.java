package com.cinemalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String zone, String mailFrom, String reminderCron) {
}
