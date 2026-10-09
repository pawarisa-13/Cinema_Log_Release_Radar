package com.cinemalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.controller.api.MovieController;
import com.cinemalog.dto.request.MovieSearchCriteria;
import com.cinemalog.dto.response.PageResponse;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.service.MovieDiscoveryService;
import com.cinemalog.service.MovieQueryService;
import com.cinemalog.service.UserProfileService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    MovieQueryService queryService;
    @MockitoBean
    MovieDiscoveryService discoveryService;
    @MockitoBean
    CurrentUserProvider currentUser;
    @MockitoBean
    UserProfileService userProfileService;

    @Test
    void searchReturnsAPageEnvelope() throws Exception {
        when(queryService.search(any(MovieSearchCriteria.class)))
                .thenReturn(new PageResponse<>(List.of(), 0, 40, 0, 0, true, true));

        mvc.perform(get("/api/v1/movies").param("q", "ghost").param("sort", "RATING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(40))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void pageSizeAbove100IsRejected() throws Exception {
        mvc.perform(get("/api/v1/movies").param("size", "500")).andExpect(status().isBadRequest());
    }

    @Test
    void unknownMovieIs404() throws Exception {
        when(queryService.getDetail(42L)).thenThrow(new ResourceNotFoundException("Movie 42 was not found."));

        mvc.perform(get("/api/v1/movies/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Movie 42 was not found."));
    }
}
