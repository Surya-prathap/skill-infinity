package com.skillinfinity.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequestDTO {

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 500, message = "Profile image URL cannot exceed 500 characters")
    private String profileImageUrl;

    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    @Size(max = 50, message = "Time zone cannot exceed 50 characters")
    private String timeZone;
}