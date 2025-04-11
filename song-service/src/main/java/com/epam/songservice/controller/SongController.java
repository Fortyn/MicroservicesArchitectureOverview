package com.epam.songservice.controller;

import com.epam.songservice.dto.CreateSongResponse;
import com.epam.songservice.dto.DeleteSongResponse;
import com.epam.songservice.dto.SongPayload;
import com.epam.songservice.dto.SongResponse;
import com.epam.songservice.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("songs")
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<CreateSongResponse> create(
            @RequestBody SongPayload payload) {
        var body = songService.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> get(@PathVariable Integer id) {
        var body = songService.get(id);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> delete(@RequestParam("id") String ids) {
        var body = songService.delete(ids);
        return ResponseEntity.ok(body);
    }
}
