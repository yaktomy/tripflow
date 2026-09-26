package com.tripflow.tripmember.dto;

public record TripMemberResponse(
        Long id,
        Long tripId,
        Long userId
) {
}