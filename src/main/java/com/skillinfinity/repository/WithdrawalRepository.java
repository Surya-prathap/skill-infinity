package com.skillinfinity.repository;

import com.skillinfinity.entity.WithdrawalRequest;
import com.skillinfinity.entity.User;
import com.skillinfinity.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<WithdrawalRequest, Long> {

    List<WithdrawalRequest> findByMentorOrderByRequestedAtDesc(User mentor);

    List<WithdrawalRequest> findByStatusOrderByRequestedAtAsc(WithdrawalStatus status);

    List<WithdrawalRequest> findByMentorAndStatusOrderByRequestedAtDesc(
            User mentor,
            WithdrawalStatus status
    );
}