package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.common.CreditDeductionResult;
import com.skillinfinity.dto.request.BookingRequestDTO;
import com.skillinfinity.dto.response.BookingResponseDTO;
import com.skillinfinity.entity.*;
import com.skillinfinity.enums.*;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.*;
import com.skillinfinity.service.BookingService;
import com.skillinfinity.service.WalletTransactionService;
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
    private final WalletTransactionService walletTransactionService;

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

        BigDecimal availableCredits = wallet.getWelcomeCredits()
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
                .welcomeCreditsUsed(
                        deductionResult.getWelcomeCreditsUsed()
                )
                .learningCreditsUsed(
                        deductionResult.getLearningCreditsUsed()
                )
                .build();

        booking = bookingRepository.save(booking);

        if (deductionResult.getPurchasedCreditsUsed()
                .compareTo(BigDecimal.ZERO) > 0) {

            walletTransactionService.createTransaction(
                    learner,
                    TransactionType.DEBIT,
                    TransactionCategory.BOOKING,
                    CreditType.PURCHASED,
                    deductionResult.getPurchasedCreditsUsed(),
                    booking.getBookingReference()
            );
        }

        if (deductionResult.getWelcomeCreditsUsed()
                .compareTo(BigDecimal.ZERO) > 0) {

            walletTransactionService.createTransaction(
                    learner,
                    TransactionType.DEBIT,
                    TransactionCategory.BOOKING,
                    CreditType.STARTER,
                    deductionResult.getWelcomeCreditsUsed(),
                    booking.getBookingReference()
            );
        }

        if (deductionResult.getLearningCreditsUsed()
                .compareTo(BigDecimal.ZERO) > 0) {

            walletTransactionService.createTransaction(
                    learner,
                    TransactionType.DEBIT,
                    TransactionCategory.BOOKING,
                    CreditType.LEARNING,
                    deductionResult.getLearningCreditsUsed(),
                    booking.getBookingReference()
            );
        }

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

        wallet.setWelcomeCredits(
                wallet.getWelcomeCredits()
                        .add(booking.getWelcomeCreditsUsed())
        );

        wallet.setLearningCredits(
                wallet.getLearningCredits()
                        .add(booking.getLearningCreditsUsed())
        );

        walletRepository.save(wallet);

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);

        if (booking.getPurchasedCreditsUsed()
                .compareTo(BigDecimal.ZERO) > 0) {

            walletTransactionService.createTransaction(
                    learner,
                    TransactionType.CREDIT,
                    TransactionCategory.BOOKING_REFUND,
                    CreditType.PURCHASED,
                    booking.getPurchasedCreditsUsed(),
                    booking.getBookingReference()
            );
        }

        if (booking.getWelcomeCreditsUsed()
                .compareTo(BigDecimal.ZERO) > 0) {

            walletTransactionService.createTransaction(
                    learner,
                    TransactionType.CREDIT,
                    TransactionCategory.BOOKING_REFUND,
                    CreditType.STARTER,
                    booking.getWelcomeCreditsUsed(),
                    booking.getBookingReference()
            );
        }

        if (booking.getLearningCreditsUsed()
                .compareTo(BigDecimal.ZERO) > 0) {

            walletTransactionService.createTransaction(
                    learner,
                    TransactionType.CREDIT,
                    TransactionCategory.BOOKING_REFUND,
                    CreditType.LEARNING,
                    booking.getLearningCreditsUsed(),
                    booking.getBookingReference()
            );
        }

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

        if (booking.getPurchasedCreditsUsed()
                .compareTo(BigDecimal.ZERO) > 0) {

            walletTransactionService.createTransaction(
                    mentor,
                    TransactionType.CREDIT,
                    TransactionCategory.MENTOR_EARNING,
                    CreditType.WITHDRAWABLE,
                    booking.getPurchasedCreditsUsed(),
                    booking.getBookingReference()
            );
        }

        return ApiResponse.success(
                "Session completed successfully.",
                null
        );
    }

    private CreditDeductionResult deductCredits(Wallet wallet, BigDecimal creditsNeeded) {

        CreditDeductionResult result = CreditDeductionResult.builder()
                .purchasedCreditsUsed(BigDecimal.ZERO)
                .welcomeCreditsUsed(BigDecimal.ZERO)
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

        BigDecimal welcomeCredits = wallet.getWelcomeCredits();

        if (welcomeCredits.compareTo(creditsNeeded) >= 0) {

            wallet.setWelcomeCredits(
                    welcomeCredits.subtract(creditsNeeded)
            );

            result.setWelcomeCreditsUsed(creditsNeeded);

            return result;
        }

        creditsNeeded = creditsNeeded.subtract(welcomeCredits);

        result.setWelcomeCreditsUsed(welcomeCredits);

        wallet.setWelcomeCredits(BigDecimal.ZERO);

        BigDecimal learningCredits = wallet.getLearningCredits();

        wallet.setLearningCredits(
                learningCredits.subtract(creditsNeeded)
        );

        result.setLearningCreditsUsed(creditsNeeded);

        return result;
    }


}
