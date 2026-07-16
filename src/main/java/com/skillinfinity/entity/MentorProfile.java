package com.skillinfinity.entity;

import com.skillinfinity.enums.MentorStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    private boolean communityContributionEnabled = false;

    @Builder.Default
    private boolean professionalSessionsEnabled = true;

    @Builder.Default
    private Integer contributionScore = 0;

    @Builder.Default
    private Integer totalCommunitySessions = 0;

    @Builder.Default
    private Integer totalReviews = 0;

    @Builder.Default
    private Double averageRating = 0.0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MentorStatus mentorStatus = MentorStatus.ACTIVE;

    @Builder.Default
    private boolean mentorPro = false;

    @Builder.Default
    private boolean verifiedMentor = false;

}