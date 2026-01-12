package pap.project.user_data.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.model.CharacterType;

import java.util.ArrayList;
import java.util.List;

public record UserData(
        @NonNull String username,
        @NonNull List<UserCharacter> userCharacters,
        @Nullable List<CharacterType> activeTeam,
        int currency,
        int eloPoints,
        int matchPlayed,
        int matchWon
) {

    public @NonNull UserData changeUserDataAfterGame(int eloChange, int currencyChange, boolean won)
    {
        return new UserData(username, userCharacters,
                activeTeam,
                currency + currencyChange,
                eloPoints + eloChange,
                matchPlayed + 1,
                won ? matchWon + 1 : matchWon);
    }

    public @NonNull UserData changeUserDataAfterDrawing(int cost, @NonNull List<UserCharacter> changeUserCharacters)
    {
        final var changeCharacters = changeUserCharacters.stream().map(UserCharacter::getCharacterType).toList();
        final var newUserCharacters = new ArrayList<>(userCharacters.stream().filter(data -> !changeCharacters.contains(data.getCharacterType())).toList());
        newUserCharacters.addAll(changeUserCharacters);
        return new UserData(username, newUserCharacters,
                activeTeam,
                currency - cost,
                eloPoints,
                matchPlayed,
                matchWon);
    }

    public @NonNull UserData changeUserDataAfterUpgrading(@NonNull UserCharacter updatedCharacter)
    {
        List<UserCharacter> newUserCharacters = userCharacters.stream()
                .map(c -> c.getId().equals(updatedCharacter.getId()) ? updatedCharacter : c)
                .toList();

        return new UserData(
                username,
                newUserCharacters,
                activeTeam,
                currency,
                eloPoints,
                matchPlayed,
                matchWon);
    }

    public @NonNull UserData changeUserDataActiveTeam(@NonNull List<CharacterType> newActiveTeam)
    {
        return new UserData(
                username,
                userCharacters,
                newActiveTeam,
                currency,
                eloPoints,
                matchPlayed,
                matchWon);
    }

}
