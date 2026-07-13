package com.skillinfinity.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileResponseDTO {

    private Long mentorId;

    private String fullName;

    private String bio;

    private String country;

    private String state;

    private String city;

    private String profileImageUrl;

    private List<String> teachingSkills;
}
