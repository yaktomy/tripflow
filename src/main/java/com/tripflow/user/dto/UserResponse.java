package com.tripflow.user.dto;

public record UserResponse(
        Long id,
        String username,
        String email
) {
}