package pap.project.user_data.model.controller;

import jakarta.validation.constraints.Positive;

public record DrawCharacterRequest(
        DrawType drawType,
        @Positive int amount
) {
}
