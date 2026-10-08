package com.cinemalog.service.job;

import com.cinemalog.service.ReminderDispatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderJob {

    private static final Logger log = LoggerFactory.getLogger(ReminderJob.class);

    private final ReminderDispatchService dispatchService;

    public ReminderJob(ReminderDispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        run();
    }

    @Scheduled(cron = "${app.reminder-cron}", zone = "${app.zone}")
    public void run() {
        try {
            int sent = dispatchService.dispatchDueReminders();
            if (sent > 0) {
                log.info("Sent {} release reminder(s)", sent);
            }
        } catch (RuntimeException ex) {
            log.error("Reminder job failed", ex);
        }
    }
}
