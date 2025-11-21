package pap.project.auth.model.controller.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pap.project.auth.model.controller.login.LoginRequest;

public record RegisterRequest(
        @NotNull @NotBlank @Size(min = LoginRequest.MIN_USERNAME_LENGTH, max = LoginRequest.MAX_USERNAME_LENGTH) String username,
        @NotNull @NotBlank @Pattern(regexp = EMAIL_PATTERN) @Size(max = MAX_EMAIL_LENGTH) String email,
        @NotNull @NotBlank @Size(min = LoginRequest.MIN_PASSWORD_LENGTH, max = LoginRequest.MAX_PASSWORD_LENGTH) String password
) {
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
}
