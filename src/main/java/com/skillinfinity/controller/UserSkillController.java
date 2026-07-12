package com.skillinfinity.controller;

import com.skillinfinity.dto.request.AddUserSkillRequestDTO;
import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.UserSkillResponseDTO;
import com.skillinfinity.service.UserSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-skills")
@RequiredArgsConstructor
public class UserSkillController {

    private final UserSkillService userSkillService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserSkillResponseDTO>> addSkill(
            @Valid @RequestBody AddUserSkillRequestDTO request) {

        return ResponseEntity.ok(userSkillService.addSkill(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSkillResponseDTO>>> getMySkills() {

        return ResponseEntity.ok(userSkillService.getMySkills());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteSkill(
            @PathVariable Long id) {

        return ResponseEntity.ok(userSkillService.deleteSkill(id));
    }

    @GetMapping("/learn")
    public ResponseEntity<ApiResponse<List<UserSkillResponseDTO>>> getLearningSkills() {

        return ResponseEntity.ok(userSkillService.getLearningSkills());
    }

    @GetMapping("/teach")
    public ResponseEntity<ApiResponse<List<UserSkillResponseDTO>>> getTeachingSkills() {

        return ResponseEntity.ok(userSkillService.getTeachingSkills());
    }

}
