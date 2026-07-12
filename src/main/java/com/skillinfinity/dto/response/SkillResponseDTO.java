package com.skillinfinity.dto.response;

import com.skillinfinity.enums.SkillCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponseDTO {

    private Long id;

    private String name;

    private SkillCategory category;

}
