package com.cinemalog.service.impl;

import java.util.List;

import com.cinemalog.domain.entity.Movie;
import com.cinemalog.domain.entity.MovieCollection;
import com.cinemalog.dto.request.CollectionRequest;
import com.cinemalog.dto.response.CollectionDetailResponse;
import com.cinemalog.dto.response.CollectionSummaryResponse;
import com.cinemalog.exception.DuplicateResourceException;
import com.cinemalog.exception.ResourceNotFoundException;
import com.cinemalog.mapper.CollectionMapper;
import com.cinemalog.repository.MovieCollectionRepository;
import com.cinemalog.repository.MovieRepository;
import com.cinemalog.repository.UserRepository;
import com.cinemalog.service.CollectionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CollectionServiceImpl implements CollectionService {

    private final MovieCollectionRepository collectionRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final CollectionMapper collectionMapper;

    public CollectionServiceImpl(MovieCollectionRepository collectionRepository, MovieRepository movieRepository,
                                 UserRepository userRepository, CollectionMapper collectionMapper) {
        this.collectionRepository = collectionRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.collectionMapper = collectionMapper;
    }

    @Override
    public List<CollectionSummaryResponse> list(Long userId) {
        return collectionRepository.findByUserIdOrderByCreatedAtAscIdAsc(userId).stream()
                .map(collectionMapper::toSummary).toList();
    }

    @Override
    public CollectionDetailResponse get(Long userId, Long collectionId) {
        return collectionMapper.toDetail(load(userId, collectionId));
    }

    @Override
    @Transactional
    public CollectionDetailResponse create(Long userId, CollectionRequest request) {
        String name = request.name().trim();
        if (collectionRepository.existsByUserIdAndNameIgnoreCase(userId, name)) {
            throw new DuplicateResourceException("You already have a collection called \"" + name + "\".");
        }
        MovieCollection saved = collectionRepository.save(
                new MovieCollection(userRepository.getReferenceById(userId), name, request.description()));
        return collectionMapper.toDetail(saved);
    }

    @Override
    @Transactional
    public CollectionDetailResponse update(Long userId, Long collectionId, CollectionRequest request) {
        MovieCollection collection = load(userId, collectionId);
        String name = request.name().trim();
        if (collectionRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, name, collectionId)) {
            throw new DuplicateResourceException("You already have a collection called \"" + name + "\".");
        }
        collection.rename(name, request.description());
        return collectionMapper.toDetail(collection);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long collectionId) {
        collectionRepository.delete(load(userId, collectionId));
    }

    @Override
    @Transactional
    public CollectionDetailResponse addMovie(Long userId, Long collectionId, Long movieId) {
        MovieCollection collection = load(userId, collectionId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie " + movieId + " was not found."));
        collection.addMovie(movie);
        return collectionMapper.toDetail(collection);
    }

    @Override
    @Transactional
    public CollectionDetailResponse removeMovie(Long userId, Long collectionId, Long movieId) {
        MovieCollection collection = load(userId, collectionId);
        if (!collection.removeMovie(movieId)) {
            throw new ResourceNotFoundException("That movie is not in \"" + collection.getName() + "\".");
        }
        return collectionMapper.toDetail(collection);
    }

    private MovieCollection load(Long userId, Long collectionId) {
        return collectionRepository.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection " + collectionId + " was not found."));
    }
}
