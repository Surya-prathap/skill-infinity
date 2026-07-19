package com.skillinfinity.service;

import com.skillinfinity.dto.request.UpdateMentorModeRequestDTO;
import com.skillinfinity.dto.response.MentorProfileResponseDTO;

public interface MentorProfileService {

    MentorProfileResponseDTO updateMentorMode(UpdateMentorModeRequestDTO request);
}