package pap.project.game_history.controller.model.load;

import org.springframework.lang.NonNull;

public record LoadErrorResponse(
        @NonNull LoadError loadError
)
{}
