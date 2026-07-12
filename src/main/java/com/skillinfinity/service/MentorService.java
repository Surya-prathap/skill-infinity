package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.MentorSearchResponseDTO;

import java.util.List;

public interface MentorService {

    ApiResponse<List<MentorSearchResponseDTO>> getMentorsBySkill(Long skillId);

}
