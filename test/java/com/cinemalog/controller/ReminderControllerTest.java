package com.cinemalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.controller.api.ReminderController;
import com.cinemalog.dto.request.ReminderRequest;
import com.cinemalog.exception.BusinessRuleException;
import com.cinemalog.service.ReminderService;
import com.cinemalog.service.UserProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReminderController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReminderControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    ReminderService reminderService;
    @MockitoBean
    CurrentUserProvider currentUser;
    @MockitoBean
    UserProfileService userProfileService;

    @BeforeEach
    void loggedIn() {
        when(currentUser.currentUserId()).thenReturn(Optional.of(1L));
        when(currentUser.requireUserId()).thenReturn(1L);
    }

    @Test
    void missingChannelIs400WithFieldErrors() throws Exception {
        mvc.perform(put("/api/v1/users/me/reminders/5").contentType(MediaType.APPLICATION_JSON).content("{\"offsetDays\":3}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("channel"));
    }

    @Test
    void businessRuleBecomes400() throws Exception {
        when(reminderService.save(eq(1L), eq(5L), any(ReminderRequest.class)))
                .thenThrow(new BusinessRuleException("Digger is already out."));

        mvc.perform(put("/api/v1/users/me/reminders/5").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"offsetDays\":3,\"channel\":\"IN_APP\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Digger is already out."));
    }

    @Test
    void deleteReturns204() throws Exception {
        mvc.perform(delete("/api/v1/users/me/reminders/5")).andExpect(status().isNoContent());
        verify(reminderService).cancel(1L, 5L);
    }
}
