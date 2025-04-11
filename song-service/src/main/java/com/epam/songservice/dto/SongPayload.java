package com.epam.songservice.dto;

import com.epam.songservice.controller.YearDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public record SongPayload(

        @NotNull(message = "Song id is required")
        Integer id,

        @NotNull(message = "Song name is required")
        @Size(min = 1, max = 100, message = "Song name should have length between {min} and {max}")
        String name,

        @NotNull(message = "Song artist is required")
        @Size(min = 1, max = 100, message = "Song artist name should have length between {min} and {max}")
        String artist,

        @NotNull(message = "Song album is required")
        @Size(min = 1, max = 100, message = "Song album name should have length between {min} and {max}")
        String album,

        @NotNull(message = "Song duration is required")
        @Pattern(regexp = "^[0-5][0-9]:[0-5][0-9]$", message = "Duration must be in mm:ss format with leading zeros")
        String duration,

        @NotNull(message = "Song year is required")
        @Range(min = 1900, max = 2099, message = "Year must be between {min} and {max}")
        @JsonDeserialize(using = YearDeserializer.class)
        Integer year
) {

}
