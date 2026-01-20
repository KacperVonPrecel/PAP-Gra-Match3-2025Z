package pap.project.characters.impl;

import pap.project.characters.model.CharacterType;
import pap.project.game.match3.Match3Block;

public class SacredCat extends OneMainBlockCharacter {
    private static final int BASE_HEALTH = 1000;
    private static final int BASE_DAMAGE = 150;

    private static final int HEALTH_PER_LEVEL_INCREASE = 100;
    private static final int DAMAGE_PER_LEVEL_INCREASE = 30;

    private static final Match3Block.BlockType MAIN_BLOCK = Match3Block.BlockType.HEMATITE;

    public SacredCat() {
        super(BASE_HEALTH, BASE_DAMAGE, HEALTH_PER_LEVEL_INCREASE, DAMAGE_PER_LEVEL_INCREASE, MAIN_BLOCK);
    }
}
