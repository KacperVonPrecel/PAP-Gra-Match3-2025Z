package pap.project.user_data.model;

import org.springframework.lang.NonNull;
import pap.project.users.characters.UserCharacter;

import java.util.List;

public record UserData(
        List<UserCharacter> userCharacters,
        int currency,
        int eloPoints,
        int matchPlayed,
        int matchWon
) {

    public @NonNull UserData changeUserDataAfterGame(int eloChange, int currencyChange, boolean won)
    {
        return new UserData(userCharacters,
                currency + currencyChange,
                eloPoints + eloChange,
                matchPlayed + 1,
                won ? matchWon + 1 : matchWon);
    }
}
