package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.ReviewRequestDTO;
import com.skillinfinity.dto.response.ReviewResponseDTO;
import com.skillinfinity.entity.Booking;
import com.skillinfinity.entity.MentorProfile;
import com.skillinfinity.entity.Review;
import com.skillinfinity.entity.User;
import com.skillinfinity.enums.BookingStatus;
import com.skillinfinity.exception.ResourceAlreadyExistsException;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.BookingRepository;
import com.skillinfinity.repository.MentorProfileRepository;
import com.skillinfinity.repository.ReviewRepository;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;

    @Override
    public ApiResponse<ReviewResponseDTO> addReview(
            ReviewRequestDTO requestDTO) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User learner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Booking booking = bookingRepository.findById(requestDTO.getBookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found."));

        // Only the learner who booked the session can review
        if (!booking.getLearner().getId().equals(learner.getId())) {
            throw new IllegalArgumentException(
                    "You can only review your own completed sessions.");
        }

        // Session must be completed
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "You can only review completed sessions.");
        }

        // Only one review per booking
        if (reviewRepository.findByBooking(booking).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Review already submitted for this booking.");
        }

        Review review = Review.builder()
                .booking(booking)
                .mentor(booking.getMentor())
                .learner(learner)
                .rating(requestDTO.getRating())
                .review(requestDTO.getReview())
                .build();

        Review savedReview = reviewRepository.save(review);

        MentorProfile mentorProfile = mentorProfileRepository
                .findByUser(booking.getMentor())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor profile not found."));

        Double averageRating =
                reviewRepository.getAverageRating(booking.getMentor());

        long totalReviews =
                reviewRepository.countByMentor(booking.getMentor());

        mentorProfile.setAverageRating(
                averageRating == null ? 0.0 : averageRating
        );

        mentorProfile.setTotalReviews((int) totalReviews);

        mentorProfileRepository.save(mentorProfile);

        ReviewResponseDTO responseDTO = ReviewResponseDTO.builder()
                .reviewId(savedReview.getId())
                .bookingId(booking.getId())
                .learnerName(learner.getFullName())
                .mentorName(booking.getMentor().getFullName())
                .rating(savedReview.getRating())
                .review(savedReview.getReview())
                .createdAt(savedReview.getCreatedAt())
                .build();

        return ApiResponse.success(
                "Review submitted successfully.",
                responseDTO
        );
    }

    @Override
    public ApiResponse<List<ReviewResponseDTO>> getMentorReviews(Long mentorId) {

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor not found."));

        List<Review> reviews =
                reviewRepository.findByMentorOrderByCreatedAtDesc(mentor);

        List<ReviewResponseDTO> response = reviews.stream()
                .map(review -> ReviewResponseDTO.builder()
                        .reviewId(review.getId())
                        .bookingId(review.getBooking().getId())
                        .learnerName(review.getLearner().getFullName())
                        .mentorName(review.getMentor().getFullName())
                        .rating(review.getRating())
                        .review(review.getReview())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();

        return ApiResponse.success(
                "Mentor reviews fetched successfully.",
                response
        );
    }

}
