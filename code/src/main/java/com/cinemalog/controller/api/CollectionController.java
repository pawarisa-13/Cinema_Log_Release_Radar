package com.cinemalog.controller.api;

import java.net.URI;
import java.util.List;

import com.cinemalog.common.CurrentUserProvider;
import com.cinemalog.dto.request.CollectionRequest;
import com.cinemalog.dto.response.CollectionDetailResponse;
import com.cinemalog.dto.response.CollectionSummaryResponse;
import com.cinemalog.service.CollectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/collections")
@Tag(name = "Collections", description = "Personal movie shelves — full CRUD")
public class CollectionController {

    private final CollectionService collectionService;
    private final CurrentUserProvider currentUser;

    public CollectionController(CollectionService collectionService, CurrentUserProvider currentUser) {
        this.collectionService = collectionService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<CollectionSummaryResponse> list() {
        return collectionService.list(currentUser.requireUserId());
    }

    @GetMapping("/{id}")
    public CollectionDetailResponse get(@PathVariable Long id) {
        return collectionService.get(currentUser.requireUserId(), id);
    }

    @PostMapping
    @Operation(summary = "Create a collection", description = "201 on success, 409 if the name is already used")
    public ResponseEntity<CollectionDetailResponse> create(@Valid @RequestBody CollectionRequest request) {
        CollectionDetailResponse created = collectionService.create(currentUser.requireUserId(), request);
        return ResponseEntity.created(URI.create("/api/v1/users/me/collections/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename / change description")
    public CollectionDetailResponse update(@PathVariable Long id, @Valid @RequestBody CollectionRequest request) {
        return collectionService.update(currentUser.requireUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        collectionService.delete(currentUser.requireUserId(), id);
    }

    @PutMapping("/{id}/movies/{movieId}")
    @Operation(summary = "Add a movie (idempotent)")
    public CollectionDetailResponse addMovie(@PathVariable Long id, @PathVariable Long movieId) {
        return collectionService.addMovie(currentUser.requireUserId(), id, movieId);
    }

    @DeleteMapping("/{id}/movies/{movieId}")
    @Operation(summary = "Remove a movie")
    public CollectionDetailResponse removeMovie(@PathVariable Long id, @PathVariable Long movieId) {
        return collectionService.removeMovie(currentUser.requireUserId(), id, movieId);
    }
}
