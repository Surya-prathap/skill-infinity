package com.skillinfinity.dto.request;

import com.skillinfinity.enums.SkillType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddUserSkillRequestDTO {

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Skill type is required")
    private SkillType skillType;
}