package com.cinemalog.service;

import com.cinemalog.dto.response.LibraryStateResponse;
import com.cinemalog.dto.response.StatsResponse;

public interface LibraryService {

    LibraryStateResponse state(Long userId);

    StatsResponse stats(Long userId);
}
