package pap.project.auth.model.controller.register;

import org.springframework.lang.NonNull;
import pap.project.auth.model.RegisterResult;

public record RegisterErrorResponse(
        @NonNull String message
) {}
