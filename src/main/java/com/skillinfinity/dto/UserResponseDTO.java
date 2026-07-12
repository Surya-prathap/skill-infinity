package com.skillinfinity.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String bio;
    private String profileImageUrl;
    private String country;
    private String state;
    private String city;
    private String timeZone;

}
