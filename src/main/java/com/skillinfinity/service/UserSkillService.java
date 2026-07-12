package com.skillinfinity.service;

import com.skillinfinity.dto.AddUserSkillRequestDTO;
import com.skillinfinity.dto.ApiResponse;
import com.skillinfinity.dto.UserSkillResponseDTO;

import java.util.List;

public interface UserSkillService {

    ApiResponse<UserSkillResponseDTO> addSkill(AddUserSkillRequestDTO request);

    ApiResponse<List<UserSkillResponseDTO>> getMySkills();

    ApiResponse<Object> deleteSkill(Long id);

    ApiResponse<List<UserSkillResponseDTO>> getLearningSkills();

    ApiResponse<List<UserSkillResponseDTO>> getTeachingSkills();

}