package com.skillinfinity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMentorModeRequestDTO {

    @NotNull
    private Boolean communityContributionEnabled;

    @NotNull
    private Boolean professionalSessionsEnabled;
}