package com.tripflow.tripmember;

import com.tripflow.tripmember.dto.TripMemberResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips/{tripId}/members")
public class TripMemberController {

    private final TripMemberService tripMemberService;

    public TripMemberController(TripMemberService tripMemberService) {
        this.tripMemberService = tripMemberService;
    }

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public TripMemberResponse addMember(
            @PathVariable Long tripId,
            @PathVariable Long userId
    ) {
        TripMember tripMember = tripMemberService.addMember(tripId, userId);

        return new TripMemberResponse(
                tripMember.getId(),
                tripMember.getTrip().getId(),
                tripMember.getUser().getId()
        );
    }

    @GetMapping
    public List<TripMemberResponse> getMembers(@PathVariable Long tripId) {
        return tripMemberService.getMembers(tripId)
                .stream()
                .map(tripMember -> new TripMemberResponse(
                        tripMember.getId(),
                        tripMember.getTrip().getId(),
                        tripMember.getUser().getId()
                ))
                .toList();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable Long tripId,
            @PathVariable Long userId
    ) {
        tripMemberService.removeMember(tripId, userId);
    }
}