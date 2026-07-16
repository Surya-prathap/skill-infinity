package com.skillinfinity.repository;

import com.skillinfinity.entity.MentorProfile;
import com.skillinfinity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorProfileRepository
        extends JpaRepository<MentorProfile, Long> {

    Optional<MentorProfile> findByUser(User user);

}