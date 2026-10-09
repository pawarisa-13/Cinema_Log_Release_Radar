package com.cinemalog.service.impl;

public final class ReminderDispatchServiceImplTestAccess {

    private ReminderDispatchServiceImplTestAccess() {
    }

    public static String message(String title, long daysLeft) {
        return ReminderDispatchServiceImpl.message(title, daysLeft);
    }
}
