package pap.project.game_history.controller.model.save;

import org.springframework.lang.NonNull;

public record SaveErrorResponse(@NonNull SaveError saveError)
{}
