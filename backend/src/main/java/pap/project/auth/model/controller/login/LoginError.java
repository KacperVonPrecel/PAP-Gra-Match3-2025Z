package pap.project.auth.model.controller.login;

import org.springframework.lang.NonNull;

public record LoginError(
        @NonNull String message
) {}
