package pap.project.users.characters;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCharactersService
{
    private final @NonNull UserCharactersRepository userCharactersRepository;

    public UserCharactersService(@NonNull UserCharactersRepository userCharactersRepository)
    {
        this.userCharactersRepository = userCharactersRepository;
    }

    public @NonNull List<UserCharacter> getUserCharacters(long userId)
    {
        return userCharactersRepository.findAllByUserId(userId);
    }
}
