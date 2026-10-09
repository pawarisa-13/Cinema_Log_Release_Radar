package com.cinemalog.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.cinemalog.dto.response.DiaryEntryResponse;
import com.cinemalog.dto.response.PageResponse;

import org.springframework.data.domain.Pageable;

public interface DiaryQueryService {

    PageResponse<DiaryEntryResponse> list(Long userId, Pageable pageable);

    List<DiaryEntryResponse> month(Long userId, YearMonth month);

    List<DiaryEntryResponse> day(Long userId, LocalDate date);

    DiaryEntryResponse get(Long userId, Long entryId);
}
