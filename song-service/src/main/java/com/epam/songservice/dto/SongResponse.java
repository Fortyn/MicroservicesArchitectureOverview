package com.epam.songservice.dto;


public record SongResponse(
        Integer id,
        String name,
        String artist,
        String album,
        String duration,
        Integer year
) {
}
