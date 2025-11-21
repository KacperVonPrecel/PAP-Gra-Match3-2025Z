package pap.project.auth.model.controller.register;

import org.springframework.lang.NonNull;

public record RegisterErrorResponse(
        @NonNull RegisterError error
) {}
