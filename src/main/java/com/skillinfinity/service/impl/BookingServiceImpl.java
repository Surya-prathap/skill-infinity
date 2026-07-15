package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.common.CreditDeductionResult;
import com.skillinfinity.dto.request.BookingRequestDTO;
import com.skillinfinity.dto.response.BookingResponseDTO;
import com.skillinfinity.entity.*;
import com.skillinfinity.enums.BookingStatus;
import com.skillinfinity.enums.SkillType;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.*;
import com.skillinfinity.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserSkillRepository userSkillRepository;
    private final WalletRepository walletRepository;

    @Transactional
    @Override
    public ApiResponse<BookingResponseDTO> bookSession(
            BookingRequestDTO requestDTO) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User learner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        User mentor = userRepository.findById(requestDTO.getMentorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mentor not found."));

        if (learner.getId().equals(mentor.getId())) {
            throw new IllegalArgumentException(
                    "You cannot book your own session.");
        }

        Skill skill = skillRepository.findById(requestDTO.getSkillId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Skill not found."));

        UserSkill userSkill = userSkillRepository
                .findByUserAndSkillAndSkillType(
                        mentor,
                        skill,
                        SkillType.TEACH
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Selected mentor does not teach this skill."));

        List<Availability> availabilitySlots =
                availabilityRepository.findByMentorAndDate(
                        mentor,
                        requestDTO.getDate()
                );

        if (availabilitySlots.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mentor is not available on the selected date.");
        }

        LocalTime endTime = requestDTO.getStartTime()
                .plusMinutes(requestDTO.getDuration());

        boolean fitsAvailability = availabilitySlots.stream()
                .anyMatch(slot ->
                        !requestDTO.getStartTime().isBefore(slot.getStartTime()) &&
                                !endTime.isAfter(slot.getEndTime())
                );

        if (!fitsAvailability) {
            throw new IllegalArgumentException(
                    "Selected time is outside mentor availability.");
        }

        List<Booking> existingBookings =
                bookingRepository.findByMentorAndDateAndStatus(
                        mentor,
                        requestDTO.getDate(),
                        BookingStatus.BOOKED
                );

        boolean hasConflict = existingBookings.stream()
                .anyMatch(booking ->
                        requestDTO.getStartTime().isBefore(booking.getEndTime()) &&
                                endTime.isAfter(booking.getStartTime())
                );

        if (hasConflict) {
            throw new IllegalArgumentException(
                    "Selected time slot is already booked.");
        }

        if (requestDTO.getDuration() < 10 ||
                requestDTO.getDuration() % 5 != 0) {

            throw new IllegalArgumentException(
                    "Duration must be at least 10 minutes and in 5-minute increments.");
        }

        BigDecimal creditsUsed = BigDecimal.valueOf(requestDTO.getDuration())
                .divide(BigDecimal.TEN);

        Wallet wallet = walletRepository.findByUser(learner)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found."));

        BigDecimal availableCredits = wallet.getStarterCredits()
                .add(wallet.getPurchasedCredits())
                .add(wallet.getLearningCredits());

        if (availableCredits.compareTo(creditsUsed) < 0) {
            throw new IllegalArgumentException("Insufficient available credits.");
        }

        CreditDeductionResult deductionResult =
                deductCredits(wallet, creditsUsed);

        walletRepository.save(wallet);

        String bookingReference =
                "SI-" + System.currentTimeMillis();

        Booking booking = Booking.builder()
                .bookingReference(bookingReference)
                .mentor(mentor)
                .learner(learner)
                .skill(skill)
                .date(requestDTO.getDate())
                .startTime(requestDTO.getStartTime())
                .endTime(endTime)
                .creditsUsed(creditsUsed)
                .purchasedCreditsUsed(
                        deductionResult.getPurchasedCreditsUsed()
                )
                .starterCreditsUsed(
                        deductionResult.getStarterCreditsUsed()
                )
                .learningCreditsUsed(
                        deductionResult.getLearningCreditsUsed()
                )
                .build();

        booking = bookingRepository.save(booking);

        BookingResponseDTO responseDTO = BookingResponseDTO.builder()
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .mentorName(mentor.getFullName())
                .learnerName(learner.getFullName())
                .skillName(skill.getSkillName())
                .date(booking.getDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .duration(requestDTO.getDuration())
                .creditsUsed(booking.getCreditsUsed())
                .status(booking.getStatus())
                .build();

        return ApiResponse.success(
                "Session booked successfully.",
                responseDTO
        );
    }

    @Override
    public ApiResponse<List<BookingResponseDTO>> getMyBookings() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User learner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        List<Booking> bookings =
                bookingRepository.findByLearnerOrderByDateAscStartTimeAsc(learner);

        List<BookingResponseDTO> response = bookings.stream()
                .map(booking -> BookingResponseDTO.builder()
                        .bookingId(booking.getId())
                        .bookingReference(booking.getBookingReference())
                        .mentorName(booking.getMentor().getFullName())
                        .learnerName(booking.getLearner().getFullName())
                        .skillName(booking.getSkill().getSkillName())
                        .date(booking.getDate())
                        .startTime(booking.getStartTime())
                        .endTime(booking.getEndTime())
                        .duration(
                                (int) java.time.Duration.between(
                                        booking.getStartTime(),
                                        booking.getEndTime()
                                ).toMinutes()
                        )
                        .creditsUsed(booking.getCreditsUsed())
                        .status(booking.getStatus())
                        .build())
                .toList();

        return ApiResponse.success(
                "Bookings fetched successfully.",
                response
        );
    }

    @Override
    public ApiResponse<List<BookingResponseDTO>> getMentorBookings() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User mentor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        List<Booking> bookings =
                bookingRepository.findByMentorOrderByDateAscStartTimeAsc(mentor);

        List<BookingResponseDTO> response = bookings.stream()
                .map(booking -> BookingResponseDTO.builder()
                        .bookingId(booking.getId())
                        .bookingReference(booking.getBookingReference())
                        .mentorName(booking.getMentor().getFullName())
                        .learnerName(booking.getLearner().getFullName())
                        .skillName(booking.getSkill().getSkillName())
                        .date(booking.getDate())
                        .startTime(booking.getStartTime())
                        .endTime(booking.getEndTime())
                        .duration(
                                (int) java.time.Duration.between(
                                        booking.getStartTime(),
                                        booking.getEndTime()
                                ).toMinutes()
                        )
                        .creditsUsed(booking.getCreditsUsed())
                        .status(booking.getStatus())
                        .build())
                .toList();

        return ApiResponse.success(
                "Mentor bookings fetched successfully.",
                response
        );
    }

    @Override
    public ApiResponse<BookingResponseDTO> getBookingById(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found."));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        if (!booking.getLearner().getId().equals(loggedInUser.getId())
                && !booking.getMentor().getId().equals(loggedInUser.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to view this booking.");
        }

        BookingResponseDTO responseDTO = BookingResponseDTO.builder()
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .mentorName(booking.getMentor().getFullName())
                .learnerName(booking.getLearner().getFullName())
                .skillName(booking.getSkill().getSkillName())
                .date(booking.getDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .duration(
                        (int) java.time.Duration.between(
                                booking.getStartTime(),
                                booking.getEndTime()
                        ).toMinutes()
                )
                .creditsUsed(booking.getCreditsUsed())
                .status(booking.getStatus())
                .build();

        return ApiResponse.success(
                "Booking fetched successfully.",
                responseDTO
        );
    }

    @Transactional
    @Override
    public ApiResponse<String> cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found."));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User learner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // Only learner can cancel
        if (!booking.getLearner().getId().equals(learner.getId())) {
            throw new IllegalArgumentException(
                    "Only the learner can cancel this booking.");
        }

        // Booking must be BOOKED
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new IllegalArgumentException(
                    "Only booked sessions can be cancelled.");
        }

        Wallet wallet = walletRepository.findByUser(learner)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found."));

        // Refund exact credits
        wallet.setPurchasedCredits(
                wallet.getPurchasedCredits()
                        .add(booking.getPurchasedCreditsUsed())
        );

        wallet.setStarterCredits(
                wallet.getStarterCredits()
                        .add(booking.getStarterCreditsUsed())
        );

        wallet.setLearningCredits(
                wallet.getLearningCredits()
                        .add(booking.getLearningCreditsUsed())
        );

        walletRepository.save(wallet);

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);

        return ApiResponse.success(
                "Booking cancelled successfully.",
                null
        );
    }

    @Transactional
    @Override
    public ApiResponse<String> completeBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found."));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User mentor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // Only mentor can complete the session
        if (!booking.getMentor().getId().equals(mentor.getId())) {
            throw new IllegalArgumentException(
                    "Only the mentor can complete this session.");
        }

        // Session must be BOOKED
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new IllegalArgumentException(
                    "Only booked sessions can be completed.");
        }

        Wallet wallet = walletRepository.findByUser(mentor)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Wallet not found."));

        // Mentor earns only purchased credits
        wallet.setWithdrawableCredits(
                wallet.getWithdrawableCredits()
                        .add(booking.getPurchasedCreditsUsed())
        );

        walletRepository.save(wallet);

        booking.setStatus(BookingStatus.COMPLETED);

        bookingRepository.save(booking);

        return ApiResponse.success(
                "Session completed successfully.",
                null
        );
    }

    private CreditDeductionResult deductCredits(Wallet wallet, BigDecimal creditsNeeded) {

        CreditDeductionResult result = CreditDeductionResult.builder()
                .purchasedCreditsUsed(BigDecimal.ZERO)
                .starterCreditsUsed(BigDecimal.ZERO)
                .learningCreditsUsed(BigDecimal.ZERO)
                .build();

        BigDecimal purchasedCredits = wallet.getPurchasedCredits();

        if (purchasedCredits.compareTo(creditsNeeded) >= 0) {

            wallet.setPurchasedCredits(
                    purchasedCredits.subtract(creditsNeeded)
            );

            result.setPurchasedCreditsUsed(creditsNeeded);

            return result;
        }

        creditsNeeded = creditsNeeded.subtract(purchasedCredits);

        result.setPurchasedCreditsUsed(purchasedCredits);

        wallet.setPurchasedCredits(BigDecimal.ZERO);

        BigDecimal starterCredits = wallet.getStarterCredits();

        if (starterCredits.compareTo(creditsNeeded) >= 0) {

            wallet.setStarterCredits(
                    starterCredits.subtract(creditsNeeded)
            );

            result.setStarterCreditsUsed(creditsNeeded);

            return result;
        }

        creditsNeeded = creditsNeeded.subtract(starterCredits);

        result.setStarterCreditsUsed(starterCredits);

        wallet.setStarterCredits(BigDecimal.ZERO);

        BigDecimal learningCredits = wallet.getLearningCredits();

        wallet.setLearningCredits(
                learningCredits.subtract(creditsNeeded)
        );

        result.setLearningCreditsUsed(creditsNeeded);

        return result;
    }


}
