package com.skillinfinity.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSearchResponseDTO {

    private Long mentorId;

    private String fullName;

    private String bio;

    private String country;

    private String state;

    private String city;

    private String profileImageUrl;
}
