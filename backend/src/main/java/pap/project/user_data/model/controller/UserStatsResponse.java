package pap.project.user_data.model.controller;

import org.springframework.lang.NonNull;

public record UserStatsResponse(
        @NonNull String username,
        int elo,
        int wins,
        int loses
) {
}
