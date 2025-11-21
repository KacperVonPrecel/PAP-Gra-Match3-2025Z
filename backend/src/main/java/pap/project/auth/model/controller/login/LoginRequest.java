package pap.project.auth.model.controller.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull @NotBlank @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH) String username,
        @NotNull @NotBlank @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH) String password
)
{
    public static final int MIN_USERNAME_LENGTH = 5;
    public static final int MAX_USERNAME_LENGTH = 20;

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 40;
}