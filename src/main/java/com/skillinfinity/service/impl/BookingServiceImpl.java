package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
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

    private void deductCredits(Wallet wallet, BigDecimal creditsNeeded) {

        BigDecimal purchasedCredits = wallet.getPurchasedCredits();

        if (purchasedCredits.compareTo(creditsNeeded) >= 0) {

            wallet.setPurchasedCredits(
                    purchasedCredits.subtract(creditsNeeded)
            );

            return;
        }

        creditsNeeded = creditsNeeded.subtract(purchasedCredits);

        wallet.setPurchasedCredits(BigDecimal.ZERO);

        BigDecimal starterCredits = wallet.getStarterCredits();

        if (starterCredits.compareTo(creditsNeeded) >= 0) {

            wallet.setStarterCredits(
                    starterCredits.subtract(creditsNeeded)
            );

            return;
        }

        creditsNeeded = creditsNeeded.subtract(starterCredits);

        wallet.setStarterCredits(BigDecimal.ZERO);

        BigDecimal learningCredits = wallet.getLearningCredits();

        wallet.setLearningCredits(
                learningCredits.subtract(creditsNeeded)
        );
    }
}
