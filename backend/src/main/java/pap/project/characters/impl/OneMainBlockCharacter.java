package pap.project.characters.impl;

import org.springframework.lang.NonNull;
import pap.project.characters.Character;
import pap.project.characters.CharacterInGame;
import pap.project.characters.model.CharacterType;
import pap.project.game.match3.Match3Block;
import pap.project.game_history.model.HistoryCharacterData;

import java.util.OptionalInt;

public class OneMainBlockCharacter implements Character
{
    private final @NonNull CharacterType characterType;

    private final int baseHealth;
    private final int baseDamage;
    private final int healthPerLevelIncrease;
    private final int damagePerLevelIncrease;

    private final @NonNull Match3Block.BlockType mainBlock;

    OneMainBlockCharacter(@NonNull CharacterType characterType, int baseHealth, int baseDamage, int healthPerLevelIncrease, int damagePerLevelIncrease,
                          @NonNull Match3Block.BlockType mainBlock)
    {
        this.characterType = characterType;
        this.baseHealth = baseHealth;
        this.baseDamage = baseDamage;
        this.healthPerLevelIncrease = healthPerLevelIncrease;
        this.damagePerLevelIncrease = damagePerLevelIncrease;
        this.mainBlock = mainBlock;
    }

    @Override
    public final int getDamage(int level)
    {
        if (level < 1)
            throw new IllegalArgumentException("Invalid level");
        return baseHealth + ((level - 1) * healthPerLevelIncrease);
    }

    @Override
    public final int getHealth(int level)
    {
        if (level < 1)
            throw new IllegalArgumentException("Invalid level");
        return baseDamage + ((level - 1) * damagePerLevelIncrease);
    }

    @Override
    public final @NonNull OptionalInt getRequiredCopiesForNextLevel(int level)
    {
        return OptionalInt.of(level * 10);
    }

    @Override
    public final @NonNull CharacterInGame createCharacterInGame(long characterId, int level)
    {
        return new OneMainBlockCharacterInGame(characterId, getDamage(level), getHealth(level), mainBlock, new HistoryCharacterData(characterType, level));
    }
}
