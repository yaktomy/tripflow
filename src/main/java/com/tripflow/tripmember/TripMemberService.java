package com.tripflow.tripmember;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.trip.Trip;
import com.tripflow.trip.TripRepository;
import com.tripflow.user.User;
import com.tripflow.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripMemberService {

    private final TripMemberRepository tripMemberRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripMemberService(
            TripMemberRepository tripMemberRepository,
            TripRepository tripRepository,
            UserRepository userRepository
    ) {
        this.tripMemberRepository = tripMemberRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public TripMember addMember(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip with id " + tripId + " not found"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + userId + " not found"
                ));

        if (tripMemberRepository.findByTripIdAndUserId(tripId, userId).isPresent()) {
            throw new IllegalArgumentException(
                    "User with id " + userId + " is already a member of trip " + tripId
            );
        }

        return tripMemberRepository.save(new TripMember(trip, user));
    }

    public List<TripMember> getMembers(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException(
                    "Trip with id " + tripId + " not found"
            );
        }

        return tripMemberRepository.findByTripId(tripId);
    }

    public void removeMember(Long tripId, Long userId) {
        TripMember member = tripMemberRepository
                .findByTripIdAndUserId(tripId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + userId + " is not a member of trip " + tripId
                ));

        tripMemberRepository.delete(member);
    }
}