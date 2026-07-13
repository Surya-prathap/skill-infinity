package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.MentorProfileResponseDTO;
import com.skillinfinity.dto.response.MentorSearchResponseDTO;
import com.skillinfinity.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<ApiResponse<List<MentorSearchResponseDTO>>> getMentorsBySkill(
            @PathVariable Long skillId) {

        return ResponseEntity.ok(mentorService.getMentorsBySkill(skillId));
    }

    @GetMapping("/{mentorId}")
    public ResponseEntity<ApiResponse<MentorProfileResponseDTO>> getMentorProfile(
            @PathVariable Long mentorId) {

        return ResponseEntity.ok(
                mentorService.getMentorProfile(mentorId));
    }

}
