package org.com.ridaa.rideshare.repository;

import org.com.ridaa.rideshare.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
    List<Ride> findByUserId(String userId);
    List<Ride> findByDriverId(String driverId);
    List<Ride> findByStatus(String status);
}
