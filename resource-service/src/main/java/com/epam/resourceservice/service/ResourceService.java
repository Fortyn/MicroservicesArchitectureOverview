package com.epam.resourceservice.service;

import com.epam.resourceservice.dto.CreateResourceResponse;
import com.epam.resourceservice.dto.DeleteResourceResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.tika.exception.TikaException;
import org.springframework.core.io.Resource;

public interface ResourceService {

    CreateResourceResponse create(byte[] bytes) throws TikaException;

    Resource download(
            @NotNull
            @Positive(message = "Invalid value ${validatedValue} for ID. Must be a positive integer")
            Integer id
    );

    DeleteResourceResponse delete(@NotBlank String ids);

}
