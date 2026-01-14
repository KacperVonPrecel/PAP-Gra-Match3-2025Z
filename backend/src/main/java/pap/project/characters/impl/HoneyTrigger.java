package pap.project.characters.impl;

import org.springframework.lang.NonNull;
import pap.project.characters.Character;
import pap.project.characters.CharacterInGame;
import pap.project.characters.model.CharacterType;
import pap.project.game.match3.Match3Block;
import pap.project.game_history.model.HistoryCharacterData;

import java.util.List;
import java.util.OptionalInt;

public class HoneyTrigger implements Character {
    private static final int BASE_HEALTH = 1000;
    private static final int BASE_DAMAGE = 40;

    private static final int HEALTH_PER_LEVEL_INCREASE = 100;
    private static final int DAMAGE_PER_LEVEL_INCREASE = 5;


    @Override
    public final int getDamage(int level)
    {
        if (level < 1)
            throw new IllegalArgumentException("Invalid level");
        return BASE_DAMAGE + ((level - 1) * DAMAGE_PER_LEVEL_INCREASE);
    }

    @Override
    public final int getHealth(int level)
    {
        if (level < 1)
            throw new IllegalArgumentException("Invalid level");
        return BASE_HEALTH + ((level - 1) * HEALTH_PER_LEVEL_INCREASE);
    }

    @Override
    public final @NonNull OptionalInt getRequiredCopiesForNextLevel(int level)
    {
        return OptionalInt.of(level * 10);
    }

    @Override
    public CharacterInGame createCharacterInGame(long characterId, int level) {
        return new HoneyTriggerInGame(characterId, getDamage(level), getHealth(level), new HistoryCharacterData(CharacterType.HONEY_TRIGGER, level));
    }
}