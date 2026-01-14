package pap.project.game;

import org.springframework.lang.NonNull;
import pap.project.game.match3.Match3Block;

import java.util.Map;

public abstract class CharacterInGame
{
    public final long characterId;

    private int currentHealth;

    public CharacterInGame(long characterId)
    {
        this.characterId = characterId;
    }

    public boolean isAlive()
    {
        return currentHealth > 0;
    }

    public void takeDamage(int damage)
    {
        if (!isAlive())
            throw new IllegalStateException("Character is already dead");
        currentHealth = Math.max(currentHealth - damage, 0);
    }

    public int getCurrentHealth()
    {
        return currentHealth;
    }

    /**
     * This method shouldn't modify internal state of object, because it should be possible to call it multiple times
     * to calculate damage from matched blocks.
     * @return damage count which character are about to deal. It is non-negative int.
     */
    public abstract int calculateDamage(@NonNull Map<Match3Block.BlockType, Integer> totalMatchedBlocks);
}
