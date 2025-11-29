package pap.project.user_data.model;

import pap.project.users.characters.UserCharacter;

import java.util.List;

public record UserData(
        List<UserCharacter> userCharacters,
        int money
) {
}
