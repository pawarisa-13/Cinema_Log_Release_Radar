package com.cinemalog.service;

import com.cinemalog.domain.entity.Reminder;

public interface ReminderDispatchService {

    int dispatchDueReminders();

    boolean dispatchIfDue(Reminder reminder);
}
