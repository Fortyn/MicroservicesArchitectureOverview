package com.epam.songservice.service;

import com.epam.songservice.dto.CreateSongResponse;
import com.epam.songservice.dto.DeleteSongResponse;
import com.epam.songservice.dto.SongPayload;
import com.epam.songservice.dto.SongResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface SongService {

    CreateSongResponse create(@Valid SongPayload payload);

    SongResponse get(@NotNull Integer id);

    DeleteSongResponse delete(@NotBlank String ids);

}
