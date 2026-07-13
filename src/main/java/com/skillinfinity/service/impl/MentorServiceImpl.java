package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.MentorProfileResponseDTO;
import com.skillinfinity.dto.response.MentorSearchResponseDTO;
import com.skillinfinity.entity.Skill;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.UserSkill;
import com.skillinfinity.enums.SkillType;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.SkillRepository;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.repository.UserSkillRepository;
import com.skillinfinity.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    @Override
    public ApiResponse<List<MentorSearchResponseDTO>> getMentorsBySkill(Long skillId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Skill not found."));

        List<UserSkill> mentorSkills =
                userSkillRepository.findBySkillAndSkillType(skill, SkillType.TEACH);

        mentorSkills = mentorSkills.stream()
                .filter(userSkill ->
                        !userSkill.getUser().getId().equals(currentUser.getId()))
                .toList();

        mentorSkills = mentorSkills.stream()
                .filter(userSkill ->
                        isProfileComplete(userSkill.getUser()))
                .toList();

        List<MentorSearchResponseDTO> response = mentorSkills.stream()
                .map(userSkill -> {

                    User mentor = userSkill.getUser();

                    return MentorSearchResponseDTO.builder()
                            .mentorId(mentor.getId())
                            .fullName(mentor.getFullName())
                            .bio(mentor.getBio())
                            .country(mentor.getCountry())
                            .state(mentor.getState())
                            .city(mentor.getCity())
                            .profileImageUrl(mentor.getProfileImageUrl())
                            .build();
                })
                .toList();

        return ApiResponse.<List<MentorSearchResponseDTO>>builder()
                .success(true)
                .message("Mentors fetched successfully.")
                .data(response)
                .build();
    }

    private boolean isProfileComplete(User user) {

        return user.getBio() != null &&
                !user.getBio().isBlank() &&

                user.getCountry() != null &&
                !user.getCountry().isBlank() &&

                user.getState() != null &&
                !user.getState().isBlank() &&

                user.getCity() != null &&
                !user.getCity().isBlank() &&

                user.getProfileImageUrl() != null &&
                !user.getProfileImageUrl().isBlank();
    }

    @Override
    public ApiResponse<MentorProfileResponseDTO> getMentorProfile(Long mentorId) {

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor not found."));

        List<UserSkill> teachingSkills = userSkillRepository
                .findByUserAndSkillType(mentor, SkillType.TEACH);

        List<String> teachingSkillNames = teachingSkills.stream()
                .map(userSkill -> userSkill.getSkill().getSkillName())
                .toList();

        MentorProfileResponseDTO response = MentorProfileResponseDTO.builder()
                .mentorId(mentor.getId())
                .fullName(mentor.getFullName())
                .bio(mentor.getBio())
                .country(mentor.getCountry())
                .state(mentor.getState())
                .city(mentor.getCity())
                .profileImageUrl(mentor.getProfileImageUrl())
                .teachingSkills(teachingSkillNames)
                .build();

        return ApiResponse.<MentorProfileResponseDTO>builder()
                .success(true)
                .message("Mentor profile fetched successfully.")
                .data(response)
                .build();
    }



}
