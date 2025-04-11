package com.epam.resourceservice.service.impl;

import com.epam.resourceservice.dto.CreateResourceResponse;
import com.epam.resourceservice.dto.DeleteResourceResponse;
import com.epam.resourceservice.dto.SongPayload;
import com.epam.resourceservice.entity.ResourceEntity;
import com.epam.resourceservice.exception.InvalidInputException;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private static final String ALBUM_METADATA_KEY = "xmpDM:album";
    private static final String ARTIST_METADATA_KEY = "xmpDM:artist";
    private static final String TITLE_METADATA_KEY = "dc:title";
    private static final String DURATION_METADATA_KEY = "xmpDM:duration";
    private static final String RELEASE_DATE_METADATA_KEY = "xmpDM:releaseDate";
    private static final int MAX_CSV_LENGTH = 200;

    private final ResourceRepository repository;

    private final SongServiceClient songServiceClient;

    @Override
    @Transactional
    public CreateResourceResponse create(byte[] bytes) throws TikaException {
        var entity = ResourceEntity.builder()
                .content(bytes)
                .build();
        var saved = repository.save(entity);
        var metadata = parseSongMetadata(saved.getId(), bytes);
        songServiceClient.saveMetadata(metadata);
        return new CreateResourceResponse(saved.getId());
    }

    @Override
    public Resource download(
            @NotNull
            @Positive(message = "Invalid value ${validatedValue} for ID. Must be a positive integer")
            Integer id
    ) {
        return repository.findById(id)
                .map(ResourceEntity::getContent)
                .map(ByteArrayResource::new)
                .orElseThrow(() -> new EntityNotFoundException("Resource with ID=%s not found".formatted(id)));
    }

    private SongPayload parseSongMetadata(Integer id, byte[] bytes) throws TikaException {
        var metadata = new Metadata();
        var parser = new Mp3Parser();
        var handler = new BodyContentHandler();
        var context = new ParseContext();
        try (var stream = new ByteArrayInputStream(bytes)) {
            parser.parse(stream, handler, metadata, context);
            var duration = formatDuration(metadata.get(DURATION_METADATA_KEY));
            return new SongPayload(
                    id,
                    metadata.get(TITLE_METADATA_KEY),
                    metadata.get(ARTIST_METADATA_KEY),
                    metadata.get(ALBUM_METADATA_KEY),
                    duration,
                    metadata.get(RELEASE_DATE_METADATA_KEY)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new TikaException("Failed to parse song metadata");
        }
    }

    private String formatDuration(String raw) {
        var secondsRaw = Double.parseDouble(raw);
        var seconds = Math.round(secondsRaw % 60);
        var minutes = Math.round(secondsRaw / 60);
        return formatLeadingZero(minutes) + ":" + formatLeadingZero(seconds);
    }

    private String formatLeadingZero(long duration) {
        return duration > 10 ? String.valueOf(duration) : "0%d".formatted(duration);
    }


    @Override
    @Transactional
    public DeleteResourceResponse delete(@NotBlank String ids) {
        if (ids.length() > MAX_CSV_LENGTH) {
            var message = "CSV string is too long: received %d characters, maximum allowed is %d".formatted(
                    ids.length(), MAX_CSV_LENGTH);
            throw new InvalidInputException(message);
        }
        var deletingIds = Arrays.stream(ids.split(",")).map(this::parseFromCSV).collect(Collectors.toSet());
        var foundIds = repository.findAllById(deletingIds)
                .stream()
                .map(ResourceEntity::getId)
                .collect(Collectors.toSet());
        if (foundIds.isEmpty()) {
            return new DeleteResourceResponse(List.of());
        }
        repository.deleteAllById(foundIds);
        songServiceClient.deleteMetadata(foundIds);
        return new DeleteResourceResponse(foundIds.stream().toList());
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
