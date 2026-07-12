package com.skillinfinity.service.impl;

import com.skillinfinity.dto.AddUserSkillRequestDTO;
import com.skillinfinity.dto.ApiResponse;
import com.skillinfinity.dto.UserSkillResponseDTO;
import com.skillinfinity.entity.Skill;
import com.skillinfinity.entity.User;
import com.skillinfinity.entity.UserSkill;
import com.skillinfinity.exception.InvalidRequestException;
import com.skillinfinity.exception.ResourceAlreadyExistsException;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.SkillRepository;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.repository.UserSkillRepository;
import com.skillinfinity.service.UserSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSkillServiceImpl implements UserSkillService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    @Override
    public ApiResponse<UserSkillResponseDTO> addSkill(AddUserSkillRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Skill not found."));

        if (userSkillRepository.existsByUserAndSkillAndSkillType(
                user,
                skill,
                request.getSkillType())) {

            throw new ResourceAlreadyExistsException(
                    "Skill already added as " + request.getSkillType());
        }

        UserSkill userSkill = UserSkill.builder()
                .user(user)
                .skill(skill)
                .skillType(request.getSkillType())
                .build();

        UserSkill saved = userSkillRepository.save(userSkill);

        UserSkillResponseDTO response = UserSkillResponseDTO.builder()
                .id(saved.getId())
                .skillId(skill.getId())
                .skillName(skill.getSkillName())
                .category(skill.getCategory())
                .skillType(saved.getSkillType())
                .build();

        return ApiResponse.<UserSkillResponseDTO>builder()
                .success(true)
                .message("Skill added successfully.")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<List<UserSkillResponseDTO>> getMySkills() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        List<UserSkill> userSkills = userSkillRepository.findByUser(user);

        List<UserSkillResponseDTO> response = userSkills.stream()
                .map(userSkill -> UserSkillResponseDTO.builder()
                        .id(userSkill.getId())
                        .skillId(userSkill.getSkill().getId())
                        .skillName(userSkill.getSkill().getSkillName())
                        .category(userSkill.getSkill().getCategory())
                        .skillType(userSkill.getSkillType())
                        .build())
                .toList();

        return ApiResponse.<List<UserSkillResponseDTO>>builder()
                .success(true)
                .message("User skills fetched successfully.")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<Object> deleteSkill(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        UserSkill userSkill = userSkillRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User skill not found."));

        if (!userSkill.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException("You are not authorized to delete this skill.");
        }

        userSkillRepository.delete(userSkill);

        return ApiResponse.builder()
                .success(true)
                .message("Skill removed successfully.")
                .data(null)
                .build();
    }

}
