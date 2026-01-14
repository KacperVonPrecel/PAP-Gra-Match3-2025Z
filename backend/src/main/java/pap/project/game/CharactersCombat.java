package pap.project.game;

import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import pap.project.characters.CharacterInGame;
import pap.project.game.match3.Match3Block;
import pap.project.game.model.CharacterCombatResult;
import pap.project.game.model.communication.PlayerCharactersState;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CharactersCombat
{
    private final @NonNull List<CharacterInGame> firstPlayerCharacters;
    private final @NonNull List<CharacterInGame> secondPlayerCharacters;

    public CharactersCombat(@NonNull List<CharacterInGame> firstPlayerCharacters, @NonNull List<CharacterInGame> secondPlayerCharacters)
    {
        this.firstPlayerCharacters = firstPlayerCharacters;
        this.secondPlayerCharacters = secondPlayerCharacters;
    }

    public @NonNull CharacterCombatResult process(boolean firstPlayerMove, @NonNull Map<Match3Block.BlockType, Integer> totalMatchedBlocks)
    {
        final Pair<Integer, Long> attackInfo = getAttackInfo(firstPlayerMove, totalMatchedBlocks);
        takeDamage(!firstPlayerMove, attackInfo.getFirst());
        return new CharacterCombatResult(getPlayerCharactersState(true), getPlayerCharactersState(false), attackInfo.getSecond());
    }

    /**
     * @param firstPlayerMove which player made move. If true that mean first player make move, and it is calculated damage from his characters.
     * @return first value is damage to deal to the opponent characters, by moving player. Second value is id of character which deal damage.
     */
    private @NonNull Pair<Integer, Long> getAttackInfo(boolean firstPlayerMove, @NonNull Map<Match3Block.BlockType, Integer> totalMatchedBlocks)
    {
        final List<CharacterInGame> aliveCharacters = (firstPlayerMove ? firstPlayerCharacters : secondPlayerCharacters)
                .stream().filter(CharacterInGame::isAlive).toList();

        if (aliveCharacters.isEmpty())
            throw new IllegalStateException("No alive characters for player");

        CharacterInGame attackingCharacter = null;
        int maxAttackDamage = 0;
        for (CharacterInGame aliveCharacter : aliveCharacters)
        {
            final int attackDamage = aliveCharacter.calculateDamage(totalMatchedBlocks);
            if (attackDamage > maxAttackDamage)
            {
                attackingCharacter = aliveCharacter;
                maxAttackDamage = attackDamage;
            }
        }

        return Pair.of(maxAttackDamage, attackingCharacter.characterId);
    }

    /**
     * After calling this method it should be checked if game have ended, because it can cause killing last player's character.
     * @param firstPlayerTakesDamage which player's characters takes damage.
     *                               If true that mean second player make move, and he is dealing damage to first player characters.
     * @param damageToTake damage dealt to the opponent characters, by moving player.
     */
    private void takeDamage(boolean firstPlayerTakesDamage, int damageToTake)
    {
        final List<CharacterInGame> aliveCharacters = (firstPlayerTakesDamage ? firstPlayerCharacters : secondPlayerCharacters)
                .stream().filter(CharacterInGame::isAlive).toList();

        if (aliveCharacters.isEmpty())
            throw new IllegalStateException("No alive characters for player");

        final CharacterInGame characterToGetDamage = aliveCharacters.getFirst();
        characterToGetDamage.takeDamage(damageToTake);
    }

    public @NonNull PlayerCharactersState getPlayerCharactersState(boolean firstPlayer)
    {
        final List<CharacterInGame> characters = (firstPlayer ? firstPlayerCharacters : secondPlayerCharacters);
        return new PlayerCharactersState(characters.stream().collect(Collectors.toMap((c) -> c.characterId, CharacterInGame::getCurrentHealth)));
    }
}
