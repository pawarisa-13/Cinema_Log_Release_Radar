package com.cinemalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.cinemalog.domain.enums.ReminderStatus;
import com.cinemalog.exception.BusinessRuleException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReminderStatusTest {

    @Test
    @DisplayName("SCHEDULED → send → SENT")
    void scheduledCanBeSent() {
        assertThat(ReminderStatus.SCHEDULED.send()).isEqualTo(ReminderStatus.SENT);
    }

    @Test
    void sentCannotBeSentTwice() {
        assertThatThrownBy(ReminderStatus.SENT::send).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cancelledCannotBeSent() {
        assertThatThrownBy(ReminderStatus.CANCELLED::send).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void everyStateCanBeRescheduledOrCancelled() {
        for (ReminderStatus s : ReminderStatus.values()) {
            assertThat(s.reschedule()).isEqualTo(ReminderStatus.SCHEDULED);
            assertThat(s.cancel()).isEqualTo(ReminderStatus.CANCELLED);
        }
    }

    @Test
    void onlyCancelledIsInactive() {
        assertThat(ReminderStatus.SCHEDULED.isActive()).isTrue();
        assertThat(ReminderStatus.SENT.isActive()).isTrue();
        assertThat(ReminderStatus.CANCELLED.isActive()).isFalse();
    }
}
