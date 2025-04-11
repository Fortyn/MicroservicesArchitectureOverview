package com.epam.songservice.service.impl;

import com.epam.songservice.dto.CreateSongResponse;
import com.epam.songservice.dto.DeleteSongResponse;
import com.epam.songservice.dto.SongPayload;
import com.epam.songservice.dto.SongResponse;
import com.epam.songservice.entity.SongEntity;
import com.epam.songservice.exception.InvalidInputException;
import com.epam.songservice.repository.SongRepository;
import com.epam.songservice.service.SongService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private static final int MAX_CSV_LENGTH = 200;
    private final SongRepository repository;

    @Override
    @Transactional
    public CreateSongResponse create(@Valid SongPayload payload) {
        var alreadyExists = repository.existsById(payload.id());
        if (alreadyExists) {
            var message = "Metadata for resource ID=%s already exists".formatted(payload.id());
            throw new EntityExistsException(message);
        }
        var songEntity = SongEntity.builder()
                .id(payload.id())
                .name(payload.name())
                .album(payload.album())
                .artist(payload.artist())
                .duration(payload.duration())
                .year(payload.year())
                .build();
        var savedEntity = repository.save(songEntity);
        return new CreateSongResponse(savedEntity.getId());
    }

    @Override
    public SongResponse get(@NotNull Integer id) {
        var entity = repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Song metadata for ID=%d not found".formatted(id)));

        return new SongResponse(entity.getId(),
                entity.getName(),
                entity.getArtist(),
                entity.getAlbum(),
                entity.getDuration(),
                entity.getYear()
        );
    }

    @Override
    public DeleteSongResponse delete(@NotBlank String ids) {
        if (ids.length() > MAX_CSV_LENGTH) {
            var message = "CSV string is too long: received %d characters, maximum allowed is %d".formatted(
                    ids.length(), MAX_CSV_LENGTH);
            throw new InvalidInputException(message);
        }
        var deletingIds = Arrays.stream(ids.split(",")).map(this::parseFromCSV).collect(Collectors.toSet());
        var foundIds = repository.findAllById(deletingIds)
                .stream()
                .map(SongEntity::getId)
                .collect(Collectors.toSet());
        repository.deleteAllById(foundIds);
        return new DeleteSongResponse(foundIds.stream().toList());
    }

    private Integer parseFromCSV(String scvItem) {
        try {
            return Integer.parseInt(scvItem);
        } catch (NumberFormatException exception) {
            var message = "Invalid ID format: '%s'. Only positive integers are allowed".formatted(scvItem);
            throw new InvalidInputException(message);
        }
    }
}
