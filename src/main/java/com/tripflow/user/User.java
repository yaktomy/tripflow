package com.tripflow.user;

import com.tripflow.tripmember.TripMember;
import jakarta.persistence.*;
import com.tripflow.trip.Trip;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "owner")
    private List<Trip> trips = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<TripMember> tripMemberships = new ArrayList<>();
    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public List<TripMember> getTripMemberships() {
        return tripMemberships;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}