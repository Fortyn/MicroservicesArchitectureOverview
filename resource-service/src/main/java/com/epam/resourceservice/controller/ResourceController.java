package com.epam.resourceservice.controller;

import com.epam.resourceservice.dto.CreateResourceResponse;
import com.epam.resourceservice.dto.DeleteResourceResponse;
import com.epam.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
@RequestMapping("resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<CreateResourceResponse> create(
            @RequestBody byte[] bytes) throws TikaException {
        var body = resourceService.create(bytes);
        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> get(@PathVariable Integer id) {
        var resource = resourceService.download(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .body(resource);
    }

    @DeleteMapping
    public ResponseEntity<DeleteResourceResponse> delete(@RequestParam("id") String ids) {
        var body = resourceService.delete(ids);
        return ResponseEntity.ok(body);
    }
}
