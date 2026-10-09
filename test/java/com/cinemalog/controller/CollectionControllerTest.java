package com.cinemalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.controller.api.CollectionController;
import com.cinemalog.dto.request.CollectionRequest;
import com.cinemalog.dto.response.CollectionDetailResponse;
import com.cinemalog.exception.DuplicateResourceException;
import com.cinemalog.service.CollectionService;
import com.cinemalog.service.UserProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CollectionController.class)
@AutoConfigureMockMvc(addFilters = false)
class CollectionControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    CollectionService collectionService;
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
    void createReturns201WithLocation() throws Exception {
        when(collectionService.create(eq(1L), any(CollectionRequest.class)))
                .thenReturn(new CollectionDetailResponse(8L, "Rainy Sunday", null, List.of(), Instant.now()));

        mvc.perform(post("/api/v1/users/me/collections").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Rainy Sunday\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/me/collections/8"))
                .andExpect(jsonPath("$.name").value("Rainy Sunday"));
    }

    @Test
    void blankNameIs400() throws Exception {
        mvc.perform(post("/api/v1/users/me/collections").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void duplicateNameIs409() throws Exception {
        when(collectionService.create(eq(1L), any(CollectionRequest.class)))
                .thenThrow(new DuplicateResourceException("You already have a collection called \"Rainy Sunday\"."));

        mvc.perform(post("/api/v1/users/me/collections").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Rainy Sunday\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteReturns204() throws Exception {
        mvc.perform(delete("/api/v1/users/me/collections/8")).andExpect(status().isNoContent());
    }
}
