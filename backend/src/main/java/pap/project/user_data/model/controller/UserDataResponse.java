package pap.project.user_data.model.controller;

import org.springframework.lang.NonNull;
import pap.project.users.characters.model.controller.CharacterData;

import java.util.List;

/**
 * @param characters objects in this list cannot have the same {@link pap.project.users.characters.model.CharacterType}.
 *                   If character is not present in this list it means that user not unlocked this character yet.
 * @param currency that player currently have. Cannot be negative.
 */
public record UserDataResponse(
        long id,
        @NonNull List<CharacterData> characters,
        int currency,
        long rankingPosition
        ) {
}
