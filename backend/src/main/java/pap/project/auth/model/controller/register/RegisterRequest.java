package pap.project.auth.model.controller.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull @NotBlank @Size(min = 5, max = 20) String username,
        @NotNull @NotBlank @Size(min = 6, max = 40) String password
) {}
