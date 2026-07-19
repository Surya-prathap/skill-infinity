package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.UpdateMentorModeRequestDTO;
import com.skillinfinity.dto.response.MentorProfileResponseDTO;
import com.skillinfinity.service.MentorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentor-profile")
@RequiredArgsConstructor
public class MentorProfileController {

    private final MentorProfileService mentorProfileService;

    @PatchMapping("/mode")
    public ApiResponse<MentorProfileResponseDTO> updateMentorMode(
            @Valid @RequestBody UpdateMentorModeRequestDTO request) {

        MentorProfileResponseDTO response =
                mentorProfileService.updateMentorMode(request);

        return ApiResponse.success(
                "Mentor mode updated successfully.",
                response
        );
    }
}