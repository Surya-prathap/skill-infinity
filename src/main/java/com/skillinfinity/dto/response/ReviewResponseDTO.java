package com.skillinfinity.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {

    private Long reviewId;

    private Long bookingId;

    private String learnerName;

    private String mentorName;

    private Integer rating;

    private String review;

    private LocalDateTime createdAt;
}