package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.response.SkillResponseDTO;
import com.skillinfinity.entity.Skill;
import com.skillinfinity.repository.SkillRepository;
import com.skillinfinity.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public ApiResponse<List<SkillResponseDTO>> getAllSkills() {

        List<Skill> skills = skillRepository.findAll();

        List<SkillResponseDTO> response = skills.stream()
                .map(skill -> SkillResponseDTO.builder()
                        .id(skill.getId())
                        .name(skill.getSkillName())
                        .category(skill.getCategory())
                        .build())
                .toList();

        return ApiResponse.<List<SkillResponseDTO>>builder()
                .success(true)
                .message("Skills fetched successfully.")
                .data(response)
                .build();
    }

}
