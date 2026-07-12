package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.SkillResponseDTO;

import java.util.List;

public interface SkillService {

    ApiResponse<List<SkillResponseDTO>> getAllSkills();

}
