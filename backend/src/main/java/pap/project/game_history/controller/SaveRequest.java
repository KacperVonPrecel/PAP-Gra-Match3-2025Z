package pap.project.game_history.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.lang.NonNull;

public record SaveRequest(
        @NonNull @Positive Long winnerId,
        @NonNull @Positive Long loserId,
        @NonNull @Positive Long finishTime
        )
{}
