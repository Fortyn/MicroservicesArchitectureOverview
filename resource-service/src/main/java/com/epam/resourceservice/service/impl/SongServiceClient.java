package com.epam.resourceservice.service.impl;

import com.epam.resourceservice.dto.SongPayload;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SongServiceClient {

    private static final String SONGS_URL = "/songs";
    private static final String SONG_SERVICE = "song-service";
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public void saveMetadata(SongPayload payload) {
        var url = getSongServiceUrl() + SONGS_URL;
        restTemplate.postForEntity(url, payload, Object.class);
    }

    public void deleteMetadata(Set<Integer> ids) {
        var csv = ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        var url = getSongServiceUrl() + SONGS_URL + "?id=" + csv;
        restTemplate.delete(url);
    }

    private URI getSongServiceUrl() {
        return discoveryClient.getInstances(SONG_SERVICE)
                .stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("Service '%s' not found".formatted(SONG_SERVICE)))
                .getUri();
    }

}
