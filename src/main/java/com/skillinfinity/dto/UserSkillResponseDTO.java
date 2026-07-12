package com.skillinfinity.dto;

import com.skillinfinity.enums.SkillCategory;
import com.skillinfinity.enums.SkillType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillResponseDTO {

    private Long id;

    private Long skillId;

    private String skillName;

    private SkillCategory category;

    private SkillType skillType;
}
