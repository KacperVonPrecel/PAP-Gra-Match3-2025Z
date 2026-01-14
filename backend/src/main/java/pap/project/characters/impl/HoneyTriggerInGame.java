package pap.project.characters.impl;

import org.springframework.lang.NonNull;
import pap.project.characters.CharacterInGame;
import pap.project.game.match3.Match3Block;
import pap.project.game_history.model.HistoryCharacterData;

import java.util.List;
import java.util.Map;

public class HoneyTriggerInGame extends CharacterInGame
{
    /**
     * It is damage per main block type of character.
     */
    private final int damage;
    private final @NonNull HistoryCharacterData historyCharacterData;


    public HoneyTriggerInGame(long characterId, int damage, int maxHealth, @NonNull HistoryCharacterData historyCharacterData)
    {
        super(characterId, maxHealth);
        this.damage = damage;
        this.historyCharacterData = historyCharacterData;
    }

    @Override
    public int calculateDamage(@NonNull Map<Match3Block.BlockType, Integer> totalMatchedBlocks)
    {
        return totalMatchedBlocks.values().stream().mapToInt(w -> w).sum() * damage;
    }

    @Override
    public @NonNull HistoryCharacterData historyCharacterData()
    {
        return historyCharacterData;
    }
}