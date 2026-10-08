package com.cinemalog.domain.enums;

import com.cinemalog.exception.BusinessRuleException;

public enum ReminderStatus {

    SCHEDULED {
        @Override
        public ReminderStatus send() {
            return SENT;
        }

        @Override
        public boolean isActive() {
            return true;
        }
    },

    SENT {
        @Override
        public ReminderStatus send() {
            throw new BusinessRuleException("This reminder was already sent.");
        }

        @Override
        public boolean isActive() {
            return true;
        }
    },

    CANCELLED {
        @Override
        public ReminderStatus send() {
            throw new BusinessRuleException("A cancelled reminder cannot be sent.");
        }

        @Override
        public boolean isActive() {
            return false;
        }
    };

    public abstract ReminderStatus send();

    public abstract boolean isActive();

    public ReminderStatus cancel() {
        return CANCELLED;
    }

    public ReminderStatus reschedule() {
        return SCHEDULED;
    }
}
