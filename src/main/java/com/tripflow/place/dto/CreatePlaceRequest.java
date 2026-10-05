package com.tripflow.place.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlaceRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String address,

        @Size(max = 1000)
        String description
) {
}