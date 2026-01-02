package pap.project.user_data.model;

import org.springframework.lang.NonNull;
import pap.project.users.characters.UserCharacter;

import java.util.List;

public record UserData(
        List<UserCharacter> userCharacters,
        List<Long> activeTeamIds,
        int currency,
        int eloPoints,
        int matchPlayed,
        int matchWon
) {

    public @NonNull UserData changeUserDataAfterGame(int eloChange, int currencyChange, boolean won)
    {
        return new UserData(userCharacters,
                activeTeamIds,
                currency + currencyChange,
                eloPoints + eloChange,
                matchPlayed + 1,
                won ? matchWon + 1 : matchWon);
    }

    public @NonNull UserData changeUserDataAfterDrawing(int cost)
    {
        return new UserData(userCharacters,
                activeTeamIds,
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
                newUserCharacters,
                activeTeamIds,
                currency,
                eloPoints,
                matchPlayed,
                matchWon);
    }

    public @NonNull UserData changeUserDataActiveTeam(@NonNull List<Long> newActiveTeamIds)
    {
        return new UserData(
                userCharacters,
                newActiveTeamIds,
                currency,
                eloPoints,
                matchPlayed,
                matchWon);
    }

}
