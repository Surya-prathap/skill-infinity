package com.skillinfinity.repository;

import com.skillinfinity.entity.Availability;
import com.skillinfinity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByMentor(User mentor);

    List<Availability> findByMentorAndDate(User mentor, LocalDate date);
}
