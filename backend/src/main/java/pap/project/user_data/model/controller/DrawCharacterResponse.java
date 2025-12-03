package pap.project.user_data.model.controller;

import java.util.List;

public record DrawCharacterResponse(
        List<DrawResultEntry> results
) {
}
