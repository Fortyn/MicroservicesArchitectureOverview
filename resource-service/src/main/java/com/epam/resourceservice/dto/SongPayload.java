package com.epam.resourceservice.dto;

public record SongPayload(

        Integer id,

        String name,

        String artist,

        String album,

        String duration,

        String year
) {

}
