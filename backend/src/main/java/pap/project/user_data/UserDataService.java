package pap.project.user_data;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.UserSessionData;
import pap.project.user_data.model.controller.StartingDataResponse;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersRepository;
import pap.project.users.characters.UserCharactersService;
import pap.project.users.characters.model.CharacterStats;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.characters.model.controller.CharacterData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserDataService
{
    private final @NonNull UserCharactersRepository userCharactersRepository;
    // XXX it should be cleaned with some interval from userSessionData.
    private final @NonNull ConcurrentHashMap<Long, UserSessionData> userSessionData = new ConcurrentHashMap<>();

    public UserDataService(@NonNull UserCharactersRepository userCharactersRepository)
    {
        this.userCharactersRepository = userCharactersRepository;
    }

    /**
     * It checks if userData is already in memory. If not it loads from db.
     * Be carefully using this function, because it sets locks for user with id.
     */
    public @NonNull UserData getUserData(long userId)
    {
        final UserSessionData userSessionData = this.userSessionData.computeIfAbsent(userId, _ -> new UserSessionData());
        userSessionData.lock();
        try
        {
            final UserData userDataInMemory = userSessionData.getUserData();
            if (userDataInMemory != null)
                return userDataInMemory;
            final List<UserCharacter> userCharacters = userCharactersRepository.findAllByUserId(userId);
            final int money = 100; //XXX load from DB.
            final UserData loadedUserData = new UserData(userCharacters, money);
            userSessionData.setUserData(loadedUserData);
            return loadedUserData;
        } finally
        {
            userSessionData.unlock();
        }
    }
}
