package com.skillinfinity.service;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.ReviewRequestDTO;
import com.skillinfinity.dto.response.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {

    ApiResponse<ReviewResponseDTO> addReview(
            ReviewRequestDTO requestDTO
    );

    ApiResponse<List<ReviewResponseDTO>> getMentorReviews(
            Long mentorId
    );

}