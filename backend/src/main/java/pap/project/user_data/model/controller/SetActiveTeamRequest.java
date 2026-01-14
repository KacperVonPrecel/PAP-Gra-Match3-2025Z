package pap.project.user_data.model.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pap.project.characters.model.CharacterType;

import java.util.List;

public record SetActiveTeamRequest(
        @NotNull
        @Size(min = 3, max = 3, message = "Team must have exactly 3 characters")
        List<CharacterType> newTeam
) {}
