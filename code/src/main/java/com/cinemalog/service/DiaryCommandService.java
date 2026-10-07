package com.cinemalog.service;

import com.cinemalog.dto.request.DiaryEntryRequest;
import com.cinemalog.dto.request.UpdateDiaryEntryRequest;
import com.cinemalog.dto.response.DiaryEntryResponse;

public interface DiaryCommandService {

    DiaryEntryResponse create(Long userId, DiaryEntryRequest request);

    DiaryEntryResponse update(Long userId, Long entryId, UpdateDiaryEntryRequest request);

    void delete(Long userId, Long entryId);
}
