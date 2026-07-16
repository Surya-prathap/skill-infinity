package com.skillinfinity.controller;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.ReviewRequestDTO;
import com.skillinfinity.dto.response.ReviewResponseDTO;
import com.skillinfinity.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponseDTO> addReview(
            @Valid @RequestBody ReviewRequestDTO requestDTO) {

        return reviewService.addReview(requestDTO);
    }

    @GetMapping("/mentor/{mentorId}")
    public ApiResponse<List<ReviewResponseDTO>> getMentorReviews(
            @PathVariable Long mentorId) {

        return reviewService.getMentorReviews(mentorId);
    }
}