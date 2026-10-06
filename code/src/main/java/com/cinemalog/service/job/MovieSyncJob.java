package com.cinemalog.service.job;

import com.cinemalog.service.MovieSyncService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MovieSyncJob {

    private static final Logger log = LoggerFactory.getLogger(MovieSyncJob.class);

    private final MovieSyncService syncService;

    public MovieSyncJob(MovieSyncService syncService) {
        this.syncService = syncService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        run();
    }

    @Scheduled(cron = "${tmdb.sync-cron}", zone = "${app.zone}")
    public void run() {
        try {
            syncService.syncGenres();
            int n = syncService.syncCatalog();
            if (n > 0) {
                log.info("TMDB sync updated {} movies", n);
            }
        } catch (RuntimeException ex) {
            log.warn("TMDB sync skipped: {}", ex.getMessage());
        }
    }
}
