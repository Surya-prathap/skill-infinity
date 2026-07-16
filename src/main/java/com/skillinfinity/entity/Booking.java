package com.skillinfinity.entity;

import com.skillinfinity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", unique = true, nullable = false)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal creditsUsed;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasedCreditsUsed = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal welcomeCreditsUsed = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal learningCreditsUsed = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.BOOKED;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
