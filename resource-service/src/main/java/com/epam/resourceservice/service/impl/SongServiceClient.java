package com.epam.resourceservice.service.impl;

import com.epam.resourceservice.dto.SongPayload;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SongServiceClient {

    private static final String SONGS_URL = "/songs";
    private final RestTemplate restTemplate;

    @Value("${song.service.url}")
    private String songServiceUrl;

    public void saveMetadata(SongPayload payload) {
        var url = songServiceUrl + SONGS_URL;
        restTemplate.postForEntity(url, payload, Object.class);
    }

    public void deleteMetadata(Set<Integer> ids) {
        var csv = ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        var url = songServiceUrl + SONGS_URL + "?id=" + csv;
        restTemplate.delete(url);
    }

}
