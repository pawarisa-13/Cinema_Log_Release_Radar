package com.cinemalog.controller.api;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.request.DiaryEntryRequest;
import com.cinemalog.dto.request.UpdateDiaryEntryRequest;
import com.cinemalog.dto.response.DiaryEntryResponse;
import com.cinemalog.dto.response.PageResponse;
import com.cinemalog.service.DiaryCommandService;
import com.cinemalog.service.DiaryQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/diary")
@Tag(name = "Diary", description = "Movies I've watched — full CRUD")
public class DiaryController {

    private final DiaryQueryService queryService;
    private final DiaryCommandService commandService;
    private final CurrentUserProvider currentUser;

    public DiaryController(DiaryQueryService queryService, DiaryCommandService commandService,
                           CurrentUserProvider currentUser) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "All entries, paged and sortable",
            description = "e.g. ?page=0&size=20&sort=rating,desc  (sortable: watchedDate, rating, createdAt)")
    public PageResponse<DiaryEntryResponse> list(
            @ParameterObject @PageableDefault(size = 20, sort = "watchedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return queryService.list(currentUser.requireUserId(), pageable);
    }

    @GetMapping("/calendar")
    @Operation(summary = "Entries of one month for the calendar, e.g. ?month=2026-10")
    public List<DiaryEntryResponse> month(@RequestParam YearMonth month) {
        return queryService.month(currentUser.requireUserId(), month);
    }

    @GetMapping("/day")
    @Operation(summary = "Entries of one day for the diary pop-up, e.g. ?date=2026-09-18")
    public List<DiaryEntryResponse> day(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return queryService.day(currentUser.requireUserId(), date);
    }

    @GetMapping("/{id}")
    public DiaryEntryResponse get(@PathVariable Long id) {
        return queryService.get(currentUser.requireUserId(), id);
    }

    @PostMapping
    @Operation(summary = "Log a movie (\"you watched it!\")", description = "201 + Location header; removes it from the watchlist")
    public ResponseEntity<DiaryEntryResponse> create(@Valid @RequestBody DiaryEntryRequest request) {
        DiaryEntryResponse created = commandService.create(currentUser.requireUserId(), request);
        return ResponseEntity.created(URI.create("/api/v1/users/me/diary/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit rating, review, date or place")
    public DiaryEntryResponse update(@PathVariable Long id, @Valid @RequestBody UpdateDiaryEntryRequest request) {
        return commandService.update(currentUser.requireUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commandService.delete(currentUser.requireUserId(), id);
    }
}
