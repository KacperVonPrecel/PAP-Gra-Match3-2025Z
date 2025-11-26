package pap.project.game_history.controller.model.load;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record LoadRequest(
        @Positive
        @Min(value = MIN_RECORD_SIZE, message = "Size must be at least 10")
        @Max(value = MAX_RECORD_SIZE, message = "Size cannot exceed 100")
        int size,

        @Positive long userId,
        @Positive long latestRecordTime
)
{
    public static final int MIN_RECORD_SIZE = 10;
    public static final int MAX_RECORD_SIZE = 100;
}
