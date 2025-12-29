package pap.project.user_data;

import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.game_history.Match;
import pap.project.game_history.MatchRepository;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.UserSessionData;
import pap.project.user_data.model.controller.DrawCharacterRequest;
import pap.project.user_data.model.controller.DrawCharacterResponse;
import pap.project.user_data.model.controller.DrawResultEntry;
import pap.project.user_data.model.controller.DrawType;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersRepository;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.characters.model.Rarity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserDataService
{
    private static final int ELO_DOWN = -10;
    private static final int ELO_UP = 20;
    private static final int CURRENCY_WINNER = 500;
    private static final int CURRENCY_LOSER = 200;

    private final @NonNull UserCharactersRepository userCharactersRepository;
    private final @NonNull MatchRepository matchRepository;
    private final @NonNull UserStatsRepository userStatsRepository;
    // XXX it should be cleaned with some interval from userSessionData.
    private final @NonNull ConcurrentHashMap<Long, UserSessionData> userSessionData = new ConcurrentHashMap<>();

    public UserDataService(
            @NonNull UserCharactersRepository userCharactersRepository,
            @NonNull MatchRepository matchRepository,
            @NonNull UserStatsRepository userStatsRepository)
    {
        this.userCharactersRepository = userCharactersRepository;
        this.matchRepository = matchRepository;
        this.userStatsRepository = userStatsRepository;
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
            userStatsRepository.updateUserStatsAfterGameEnd(
                    winnerUserSessionData.getUserData().matchPlayed(),
                    winnerUserSessionData.getUserData().matchWon(),
                    winnerUserSessionData.getUserData().eloPoints(),
                    winnerUserSessionData.getUserData().currency(),
                    winnerId
                    );

            final UserData loserNewUserData = loserUserSessionData.getUserData().changeUserDataAfterGame(ELO_DOWN, CURRENCY_LOSER, false);
            loserUserSessionData.setUserData(loserNewUserData);
            userStatsRepository.updateUserStatsAfterGameEnd(
                    loserUserSessionData.getUserData().matchPlayed(),
                    loserUserSessionData.getUserData().matchWon(),
                    loserUserSessionData.getUserData().eloPoints(),
                    loserUserSessionData.getUserData().currency(),
                    loserId
                    );

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
    
    @Transactional
    public DrawCharacterResponse drawCharacters(@NonNull DrawCharacterRequest request, long userId)
    {
        final UserSessionData userDataSession = userSessionData.computeIfAbsent(userId, _ -> new UserSessionData());
        userDataSession.lock();
        try
        {
            final int cost = switch(request.drawType())
            {
                case COMMON -> 25;
                case UNCOMMON -> 50;
                case RARE -> 100;
            } * request.amount();

            if (cost > userDataSession.getUserData().currency())
                throw new RuntimeException("XXX");

            //XXX save to db result (new characters and money update
            userDataSession.setUserData(userDataSession.getUserData().changeUserDataAfterDrawing(cost));

            List<DrawResultEntry> drawResults = Stream.generate(() -> {
                Rarity drawRarity = drawRarity(request.drawType());
                return drawCharacterByRarity(drawRarity);
            })
                    .limit(request.amount())
                    .collect(Collectors.toMap(w -> w, _ -> 1, Integer::sum))
                    .entrySet().stream()
                    .map(e -> new DrawResultEntry(e.getKey(), e.getValue()))
                    .toList();

            return new DrawCharacterResponse(drawResults);
        } finally {
            userDataSession.unlock();
        }
    }

    /**
     * This function don't lock for user id.
     * @param userSessionData this function change state of given object by calling {@link UserSessionData#setUserData(UserData)} for this object.
     */
    private void loadUserSessionData(@NonNull UserSessionData userSessionData, long userId)
    {
        final List<UserCharacter> userCharacters = userCharactersRepository.findAllByUserId(userId);
        final UserStats userData = userStatsRepository.findUserStatsByUserId(userId).orElseThrow();
        final UserData loadedUserData = new UserData(userCharacters, userData.getCurrency(), userData.getEloPoints(), userData.getMatchPlayed(), userData.getMatchWon());
        userSessionData.setUserData(loadedUserData);
    }

    /**
     * This function draws rarity to draw character based on chosen DrawType
     * @param drawType chosen DrawType from which to get drop rates
     * @return Rarity from which to draw a character
     */
    private Rarity drawRarity(@NonNull DrawType drawType)
    {
        Map<Rarity, Integer> rates = drawType.getDropRates();
        int totalWeight = rates.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = new Random().nextInt(totalWeight);

        int currentSum = 0;
        for (Map.Entry<Rarity, Integer> entry : rates.entrySet())
        {
            currentSum += entry.getValue();
            if (randomValue < currentSum)
            {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Error in weight config");
    }

    /**
     * This function draws a character from a pool of characters of the same rarity
     * @param rarity this indicates what characters are in the pool
     * @return draws character with an equal probability from the pool
     */
    private CharacterType drawCharacterByRarity(@NonNull Rarity rarity)
    {
        List<CharacterType> pool = Arrays.stream(CharacterType.values())
                .filter(c -> c.getRarity() == rarity)
                .toList();

        if (pool.isEmpty())
        {
            throw new RuntimeException("No character for rarity " + rarity);
        }

        return pool.get(new Random().nextInt(pool.size()));
    }
}
