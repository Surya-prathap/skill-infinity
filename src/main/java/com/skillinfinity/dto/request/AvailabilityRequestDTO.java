package com.skillinfinity.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityRequestDTO {

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
