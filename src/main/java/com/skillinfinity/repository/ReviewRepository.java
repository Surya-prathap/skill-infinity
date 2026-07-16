package com.skillinfinity.repository;

import com.skillinfinity.entity.Booking;
import com.skillinfinity.entity.Review;
import com.skillinfinity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByBooking(Booking booking);

    List<Review> findByMentorOrderByCreatedAtDesc(User mentor);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.mentor = :mentor")
    Double getAverageRating(User mentor);

    long countByMentor(User mentor);

}