package com.skillinfinity.service;

import com.skillinfinity.dto.ApiResponse;
import com.skillinfinity.dto.SkillResponseDTO;

import java.util.List;

public interface SkillService {

    ApiResponse<List<SkillResponseDTO>> getAllSkills();

}
