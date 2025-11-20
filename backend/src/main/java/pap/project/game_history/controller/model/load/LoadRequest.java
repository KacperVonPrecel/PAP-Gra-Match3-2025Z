package pap.project.game_history.controller.model.load;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

public record LoadRequest(
        @NonNull @Positive @Size(min = MIN_RECORD_SIZE, max = MAX_RECORD_SIZE) Integer size,
        @NonNull @Positive Integer page,
        @NonNull @Positive Long userId,
        @NonNull @Positive Long latestRecordTime
)
{
    public static final int MIN_RECORD_SIZE = 10;
    public static final int MAX_RECORD_SIZE = 100;
}
