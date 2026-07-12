package com.skillinfinity.service;

import com.skillinfinity.dto.request.AddUserSkillRequestDTO;
import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.UserSkillResponseDTO;

import java.util.List;

public interface UserSkillService {

    ApiResponse<UserSkillResponseDTO> addSkill(AddUserSkillRequestDTO request);

    ApiResponse<List<UserSkillResponseDTO>> getMySkills();

    ApiResponse<Object> deleteSkill(Long id);

    ApiResponse<List<UserSkillResponseDTO>> getLearningSkills();

    ApiResponse<List<UserSkillResponseDTO>> getTeachingSkills();

}