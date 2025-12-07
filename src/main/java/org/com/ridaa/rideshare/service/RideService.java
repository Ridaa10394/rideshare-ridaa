package org.com.ridaa.rideshare.service;

import org.com.ridaa.rideshare.dto.CreateRideRequest;
import org.com.ridaa.rideshare.dto.RideResponse;
import org.com.ridaa.rideshare.exception.BadRequestException;
import org.com.ridaa.rideshare.exception.NotFoundException;
import org.com.ridaa.rideshare.model.Ride;
import org.com.ridaa.rideshare.model.User;
import org.com.ridaa.rideshare.repository.RideRepository;
import org.com.ridaa.rideshare.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideService(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    // Create a new ride request (USER)
    public RideResponse createRide(CreateRideRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Ride ride = new Ride();
        ride.setUserId(user.getId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDropLocation(request.getDropLocation());
        ride.setStatus("REQUESTED");
        ride.setCreatedAt(new Date());

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    // Get all pending ride requests (DRIVER)
    public List<RideResponse> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Driver accepts a ride
    public RideResponse acceptRide(String rideId) {
        String username = getCurrentUsername();
        User driver = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("REQUESTED")) {
            throw new BadRequestException("Ride is not in REQUESTED status");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus("ACCEPTED");

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    // Complete a ride (USER or DRIVER)
    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("ACCEPTED")) {
            throw new BadRequestException("Ride must be ACCEPTED before completing");
        }

        ride.setStatus("COMPLETED");

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    // Get rides for logged in user
    public List<RideResponse> getMyRides() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return rideRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private RideResponse mapToResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getUserId(),
                ride.getDriverId(),
                ride.getPickupLocation(),
                ride.getDropLocation(),
                ride.getStatus(),
                ride.getCreatedAt()
        );
    }
}
