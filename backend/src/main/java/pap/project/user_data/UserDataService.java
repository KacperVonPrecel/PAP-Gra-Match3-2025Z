package pap.project.user_data;

import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.game_history.Match;
import pap.project.game_history.MatchRepository;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.UserSessionData;
import pap.project.users.UserRepository;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserDataService
{
    private static final int ELO_DOWN = -10;
    private static final int ELO_UP = 20;
    private static final int CURRENCY_WINNER = 500;
    private static final int CURRENCY_LOSER = 200;

    private final @NonNull UserRepository userRepository;
    private final @NonNull UserCharactersRepository userCharactersRepository;
    private final @NonNull MatchRepository matchRepository;
    // XXX it should be cleaned with some interval from userSessionData.
    private final @NonNull ConcurrentHashMap<Long, UserSessionData> userSessionData = new ConcurrentHashMap<>();

    public UserDataService(
            @NonNull UserRepository userRepository,
            @NonNull UserCharactersRepository userCharactersRepository,
            @NonNull MatchRepository matchRepository)
    {
        this.userRepository = userRepository;
        this.userCharactersRepository = userCharactersRepository;
        this.matchRepository = matchRepository;
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
            if (userSessionData.getUserData() == null)
                loadUserSessionData(userSessionData, userId);
            return userSessionData.getUserData();
        } finally
        {
            userSessionData.unlock();
        }
    }

    /**
     * This function can take long time to execute, so don't call this in thread which need to do something else.
     */
    @Transactional
    public void processGameEnd(long winnerId, long loserId, long finishTime)
    {
        final UserSessionData winnerUserSessionData = userSessionData.computeIfAbsent(winnerId, _ -> new UserSessionData());
        final UserSessionData loserUserSessionData  = userSessionData.computeIfAbsent(loserId, _ -> new UserSessionData());
        winnerUserSessionData.lock();
        final boolean successful = loserUserSessionData.lockWithTimeout(5000);
        if (!successful)
        {
            winnerUserSessionData.unlock();
            try
            {
                // Give time for another tasks to lock winner user data if loser is still locked.
                wait(1000);
            } catch (InterruptedException wyj)
            {
                Thread.interrupted();
            }
            processGameEnd(winnerId, loserId, finishTime);
        }

        try
        {
            if (winnerUserSessionData.getUserData() == null)
                loadUserSessionData(winnerUserSessionData, winnerId);
            if (loserUserSessionData.getUserData() == null)
                loadUserSessionData(loserUserSessionData, loserId);

            final UserData winnerNewUserData = winnerUserSessionData.getUserData().changeUserDataAfterGame(ELO_UP, CURRENCY_WINNER, true);
            winnerUserSessionData.setUserData(winnerNewUserData);
            userRepository.updateUserAfterEndGame(winnerUserSessionData.getUserData().eloPoints(), winnerUserSessionData.getUserData().currency(), winnerId);

            final UserData loserNewUserData = loserUserSessionData.getUserData().changeUserDataAfterGame(ELO_DOWN, CURRENCY_LOSER, false);
            loserUserSessionData.setUserData(loserNewUserData);
            userRepository.updateUserAfterEndGame(loserUserSessionData.getUserData().eloPoints(), loserUserSessionData.getUserData().currency(), winnerId);;

            final Match match = new Match(
                    winnerId,
                    loserId,
                    finishTime,
                    ELO_UP,
                    ELO_DOWN
            );
            matchRepository.save(match);
        } finally
        {
            winnerUserSessionData.unlock();
            loserUserSessionData.unlock();
        }
    }

    /**
     * This function don't lock for user id.
     * @param userSessionData this function change state of given object by calling {@link UserSessionData#setUserData(UserData)} for this object.
     */
    private void loadUserSessionData(@NonNull UserSessionData userSessionData, long userId)
    {
        final List<UserCharacter> userCharacters = userCharactersRepository.findAllByUserId(userId);
        final int money = 100; //XXXK load from DB.
        final UserData loadedUserData = new UserData(userCharacters, money, 0, 0, 0); //XXXK load from DB.
        userSessionData.setUserData(loadedUserData);

    }
}
