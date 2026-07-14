package com.skillinfinity.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {

    private Long mentorId;

    private Long skillId;

    private LocalDate date;

    private LocalTime startTime;

    private Integer duration;
}
