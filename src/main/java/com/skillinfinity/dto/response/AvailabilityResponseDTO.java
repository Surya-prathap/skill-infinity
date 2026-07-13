package com.skillinfinity.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponseDTO {

    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
