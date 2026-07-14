package com.skillinfinity.repository;

import com.skillinfinity.entity.Booking;
import com.skillinfinity.entity.User;
import com.skillinfinity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByLearner(User learner);

    List<Booking> findByMentor(User mentor);

    List<Booking> findByMentorAndDateAndStatus(
            User mentor,
            LocalDate date,
            BookingStatus status
    );
}
