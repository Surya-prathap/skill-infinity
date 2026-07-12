package com.skillinfinity.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long id;

    private String fullName;

    private String email;

    private String token;
}
