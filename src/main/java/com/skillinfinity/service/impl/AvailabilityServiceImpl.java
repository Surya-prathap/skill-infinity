package com.skillinfinity.service.impl;

import com.skillinfinity.dto.common.ApiResponse;
import com.skillinfinity.dto.request.AvailabilityRequestDTO;
import com.skillinfinity.dto.response.AvailabilityResponseDTO;
import com.skillinfinity.entity.Availability;
import com.skillinfinity.entity.User;
import com.skillinfinity.exception.ResourceNotFoundException;
import com.skillinfinity.repository.AvailabilityRepository;
import com.skillinfinity.repository.UserRepository;
import com.skillinfinity.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<AvailabilityResponseDTO> addAvailability(
            AvailabilityRequestDTO requestDTO) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User mentor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        if (requestDTO.getStartTime().isAfter(requestDTO.getEndTime())
                || requestDTO.getStartTime().equals(requestDTO.getEndTime())) {

            throw new IllegalArgumentException(
                    "End time must be greater than start time.");
        }

        List<Availability> existingSlots = availabilityRepository
                .findByMentorAndDate(mentor, requestDTO.getDate());

        for (Availability slot : existingSlots) {

            boolean isOverlapping =
                    requestDTO.getStartTime().isBefore(slot.getEndTime()) &&
                            requestDTO.getEndTime().isAfter(slot.getStartTime());

            if (isOverlapping) {
                throw new IllegalArgumentException(
                        "Availability overlaps with an existing time slot.");
            }
        }

            Availability availability = Availability.builder()
                    .mentor(mentor)
                    .date(requestDTO.getDate())
                    .startTime(requestDTO.getStartTime())
                    .endTime(requestDTO.getEndTime())
                    .build();

            availability = availabilityRepository.save(availability);

            AvailabilityResponseDTO responseDTO = AvailabilityResponseDTO.builder()
                    .id(availability.getId())
                    .date(availability.getDate())
                    .startTime(availability.getStartTime())
                    .endTime(availability.getEndTime())
                    .build();

        return ApiResponse.success(
                "Availability added successfully.",
                responseDTO
        );
    }
}
