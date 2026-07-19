package com.skillinfinity.service.impl;

import com.skillinfinity.dto.request.UpdateMentorModeRequestDTO;
import com.skillinfinity.dto.response.MentorProfileResponseDTO;
import com.skillinfinity.entity.MentorProfile;
import com.skillinfinity.entity.User;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.MentorProfileRepository;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.service.MentorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorProfileServiceImpl implements MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;

    @Override
    public MentorProfileResponseDTO updateMentorMode(
            UpdateMentorModeRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        MentorProfile mentorProfile = mentorProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found."));

        mentorProfile.setCommunityContributionEnabled(
                request.getCommunityContributionEnabled());

        mentorProfile.setProfessionalSessionsEnabled(
                request.getProfessionalSessionsEnabled());

        MentorProfile updatedProfile =
                mentorProfileRepository.save(mentorProfile);

        return MentorProfileResponseDTO.builder()
                .mentorId(user.getId())
                .fullName(user.getFullName())
                .communityContributionEnabled(
                        updatedProfile.isCommunityContributionEnabled())
                .professionalSessionsEnabled(
                        updatedProfile.isProfessionalSessionsEnabled())
                .contributionScore(updatedProfile.getContributionScore())
                .totalCommunitySessions(
                        updatedProfile.getTotalCommunitySessions())
                .totalReviews(updatedProfile.getTotalReviews())
                .averageRating(updatedProfile.getAverageRating())
                .mentorStatus(updatedProfile.getMentorStatus())
                .mentorPro(updatedProfile.isMentorPro())
                .verifiedMentor(updatedProfile.isVerifiedMentor())
                .build();
    }
}