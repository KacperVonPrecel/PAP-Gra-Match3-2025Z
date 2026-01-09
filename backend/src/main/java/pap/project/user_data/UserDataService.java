package pap.project.user_data;

import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pap.project.game_history.Match;
import pap.project.game_history.MatchCharacters;
import pap.project.game_history.MatchCharactersRepository;
import pap.project.game_history.MatchRepository;
import pap.project.game_history.model.HistoryCharacterData;
import pap.project.user_data.model.controller.UserStatsResponse;
import pap.project.user_data.model.UserData;
import pap.project.user_data.model.UserSessionData;
import pap.project.user_data.model.controller.*;
import pap.project.user_stats.UserStats;
import pap.project.user_stats.UserStatsRepository;
import pap.project.users.UserRepository;
import pap.project.users.characters.UserCharacter;
import pap.project.users.characters.UserCharactersRepository;
import pap.project.users.characters.UserCharactersService;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.characters.model.Rarity;
import pap.project.users.characters.model.controller.CharacterData;

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
    private static final int BASE_CHARACTER_LEVEL = 1;

    private final @NonNull UserCharactersRepository userCharactersRepository;
    private final @NonNull MatchRepository matchRepository;
    private final @NonNull UserStatsRepository userStatsRepository;
    private final @NonNull MatchCharactersRepository matchCharactersRepository;
    private final @NonNull UserCharactersService userCharactersService;
    private final @NonNull UserRepository userRepository;
    // XXX it should be cleaned with some interval from userSessionData.
    private final @NonNull ConcurrentHashMap<Long, UserSessionData> userSessionData = new ConcurrentHashMap<>();

    public UserDataService(
            @NonNull UserCharactersRepository userCharactersRepository,
            @NonNull MatchRepository matchRepository,
            @NonNull UserStatsRepository userStatsRepository,
            @NonNull MatchCharactersRepository matchCharactersRepository,
            @NonNull UserCharactersService userCharactersService,
            @NonNull UserRepository userRepository)
    {
        this.userCharactersRepository = userCharactersRepository;
        this.matchRepository = matchRepository;
        this.userStatsRepository = userStatsRepository;
        this.matchCharactersRepository = matchCharactersRepository;
        this.userCharactersService = userCharactersService;
        this.userRepository = userRepository;
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
    public void processGameEnd(long winnerId, long loserId, long finishTime, List<HistoryCharacterData> winnerCharacters, List<HistoryCharacterData> loserCharacters)
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
            processGameEnd(winnerId, loserId, finishTime, winnerCharacters, loserCharacters);
        }

        try
        {
            if (winnerUserSessionData.getUserData() == null)
                loadUserSessionData(winnerUserSessionData, winnerId);
            if (loserUserSessionData.getUserData() == null)
                loadUserSessionData(loserUserSessionData, loserId);

            // TODO XXXK: Add elo and currency changes calculation

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
            final MatchCharacters matchCharacters = new MatchCharacters(
                    match,
                    winnerCharacters,
                    loserCharacters
            );

            matchCharactersRepository.save(matchCharacters);
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

            List<DrawResultEntry> drawResults = Stream.generate(() -> {
                Rarity drawRarity = drawRarity(request.drawType());
                return drawCharacterByRarity(drawRarity);
            })
                    .limit(request.amount())
                    .collect(Collectors.toMap(w -> w, _ -> 1, Integer::sum))
                    .entrySet().stream()
                    .map(e -> new DrawResultEntry(e.getKey(), e.getValue()))
                    .toList();

            Set<CharacterType> drawnTypes = drawResults.stream()
                    .map(DrawResultEntry::characterType)
                    .collect(Collectors.toSet());

            List<UserCharacter> existingCharacters = userCharactersRepository.findByUserIdAndCharacterTypeIn(userId, drawnTypes);

            Map<CharacterType, UserCharacter> characterInventoryMap = existingCharacters.stream()
                    .collect(Collectors.toMap(UserCharacter::getCharacterType, c -> c));

            List<UserCharacter> charactersToSave = new ArrayList<>();

            for (DrawResultEntry entry : drawResults)
            {
                CharacterType type = entry.characterType();
                int count = entry.amount();

                UserCharacter character = characterInventoryMap.get(type);
                if (character == null)
                {
                    character = new UserCharacter(type, userId, BASE_CHARACTER_LEVEL, count);
                    characterInventoryMap.put(type, character);
                } else
                {
                    character.setCopiesCount(character.getCopiesCount() + count);
                }
                charactersToSave.add(character);
            }
            userDataSession.setUserData(userDataSession.getUserData().changeUserDataAfterDrawing(cost));

            userStatsRepository.updateUserStatsAfterDrawing(userDataSession.getUserData().currency(), userId);
            userCharactersRepository.saveAll(charactersToSave);



            return new DrawCharacterResponse(drawResults);
        } finally {
            userDataSession.unlock();
        }
    }

    public UpgradeCharacterResponse upgradeCharacter(@NonNull UpgradeCharacterRequest request, long userId)
    {
        final UserSessionData userDataSession = userSessionData.computeIfAbsent(userId, _ -> new UserSessionData());
        userDataSession.lock();
        try
        {
            if (userDataSession.getUserData() == null)
                loadUserSessionData(userDataSession, userId);

            final UserCharacter userCharacter = userDataSession.getUserData().userCharacters().stream().filter((character) -> character.getCharacterType() == request.characterType()).findFirst().orElseThrow();

            final CharacterData characterDataToUpgrade = userCharactersService.createCharacterData(userCharacter);

            if (userCharacter.getCopiesCount() < characterDataToUpgrade.requiredCopiesForNextLevel().orElseThrow())
                throw new IllegalArgumentException("You don't have enough copies for this character");

            userCharacter.setCopiesCount(userCharacter.getCopiesCount() - characterDataToUpgrade.requiredCopiesForNextLevel().orElseThrow());
            userCharacter.setLevel(userCharacter.getLevel() + 1);
            userCharactersRepository.save(userCharacter);

            userDataSession.setUserData(userDataSession.getUserData().changeUserDataAfterUpgrading(userCharacter));

            return new UpgradeCharacterResponse(userCharactersService.createCharacterData(userCharacter));
        } finally {
            userDataSession.unlock();
        }
    }

    @Transactional
    public SetActiveTeamResponse setActiveTeam(@NonNull SetActiveTeamRequest request, long userId)
    {
        if (new HashSet<>(request.newTeamIds()).size() < request.newTeamIds().size())
        {
            throw new IllegalArgumentException("Team cannot contain any duplicate characters");
        }

        final UserSessionData userDataSession = userSessionData.computeIfAbsent(userId, _ -> new UserSessionData());
        userDataSession.lock();
        try
        {
            List<UserCharacter> selectedCharacters = userCharactersRepository.findByUserIdAndIdIn(userId, request.newTeamIds());
            if (selectedCharacters.size() != request.newTeamIds().size()) {
                throw new SecurityException("You don't have some of the selected characters");
            }

            userStatsRepository.updateUserStatsActiveTeam(request.newTeamIds(), userId);
            userDataSession.setUserData(userDataSession.getUserData().changeUserDataActiveTeam(request.newTeamIds()));

            return new SetActiveTeamResponse(userDataSession.getUserData().activeTeamIds());

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
        final UserStats userData = userStatsRepository.findUserStatsById(userId).orElseThrow();
        final String username = userRepository.findById(userId).orElseThrow().getUsername();
        final UserData loadedUserData = new UserData(username, userCharacters, userData.getActiveTeamIds(), userData.getCurrency(), userData.getEloPoints(), userData.getMatchPlayed(), userData.getMatchWon());
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
