package com.skillinfinity.dto.response;

import com.skillinfinity.enums.MentorStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileResponseDTO {

    private Long mentorId;

    private String fullName;

    private String bio;

    private String country;

    private String state;

    private String city;

    private String profileImageUrl;

    private List<String> teachingSkills;

    // Community & Professional Mentor Details
    private boolean communityContributionEnabled;

    private boolean professionalSessionsEnabled;

    private Integer contributionScore;

    private Integer totalCommunitySessions;

    // Review Details
    private Integer totalReviews;

    private Double averageRating;

    // Mentor Status
    private MentorStatus mentorStatus;

    private boolean mentorPro;

    private boolean verifiedMentor;
}
