package pap.project.characters.impl;

import org.springframework.lang.NonNull;
import pap.project.characters.CharacterInGame;
import pap.project.game.match3.Match3Block;
import pap.project.game_history.model.HistoryCharacterData;

import java.util.Map;

public class OneMainBlockCharacterInGame extends CharacterInGame
{
    /**
     * It is damage per main block type of character.
     */
    private final int damage;
    private final Match3Block.BlockType mainBlock;
    private final @NonNull HistoryCharacterData historyCharacterData;

    public OneMainBlockCharacterInGame(long characterId, int damage, int maxHealth, @NonNull Match3Block.BlockType mainBlock, @NonNull HistoryCharacterData historyCharacterData)
    {
        super(characterId, maxHealth);
        this.damage = damage;
        this.mainBlock = mainBlock;
        this.historyCharacterData = historyCharacterData;
    }

    @Override
    public int calculateDamage(@NonNull Map<Match3Block.BlockType, Integer> totalMatchedBlocks)
    {
        if (totalMatchedBlocks.containsKey(mainBlock))
            return totalMatchedBlocks.get(mainBlock) * damage;
        return totalMatchedBlocks.values().stream().mapToInt(w -> w).sum() * 10;
    }

    @Override
    public @NonNull HistoryCharacterData historyCharacterData()
    {
        return historyCharacterData;
    }
}
