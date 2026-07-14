package com.skillinfinity.dto.response;

import com.skillinfinity.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long bookingId;

    private String bookingReference;

    private String mentorName;

    private String learnerName;

    private String skillName;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer duration;

    private BigDecimal creditsUsed;

    private BookingStatus status;
}
