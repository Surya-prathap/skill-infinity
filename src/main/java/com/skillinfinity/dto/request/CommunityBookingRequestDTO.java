package com.skillinfinity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityBookingRequestDTO {

    @NotNull
    private Long mentorId;

    @NotNull
    private Long skillId;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startTime;
}