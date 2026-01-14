package pap.project.characters.impl;

import pap.project.characters.model.CharacterType;
import pap.project.game.match3.Match3Block;

public class AmethystEnchantress extends OneMainBlockCharacter
{
    private static final int BASE_HEALTH = 1000;
    private static final int BASE_DAMAGE = 50;

    private static final int HEALTH_PER_LEVEL_INCREASE = 100;
    private static final int DAMAGE_PER_LEVEL_INCREASE = 10;

    private static final Match3Block.BlockType MAIN_BLOCK = Match3Block.BlockType.AMETHYST;

    public AmethystEnchantress()
    {
        super(CharacterType.AMETHYST_ENCHANTRESS, BASE_HEALTH, BASE_DAMAGE, HEALTH_PER_LEVEL_INCREASE, DAMAGE_PER_LEVEL_INCREASE, MAIN_BLOCK);
    }
}
