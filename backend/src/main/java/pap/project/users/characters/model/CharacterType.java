package pap.project.users.characters.model;

import pap.project.game.match3.Match3Block;

public enum CharacterType
{
    AMETHYST_ENCHANTRESS(Rarity.COMMON, Match3Block.BlockType.AMETHYST),
    TRASH_MAN(Rarity.COMMON, Match3Block.BlockType.HEMATITE),
    SACRED_CAT(Rarity.UNCOMMON, Match3Block.BlockType.CITRINE),
    EMERALD_CORE_KNIGHT(Rarity.UNCOMMON,  Match3Block.BlockType.EMERALD),
    RUBY_HORNED_DAME(Rarity.RARE, Match3Block.BlockType.RUBY),
    EXPERIENCED_SWORDMAN(Rarity.RARE, Match3Block.BlockType.DIAMOND),
    HONEY_TRIGGER(Rarity.RARE, Match3Block.BlockType.CITRINE);

    private final Rarity rarity;
    private final Match3Block.BlockType mainBlockType;

    CharacterType(final Rarity rarity,  final Match3Block.BlockType mainBlockType)
    {
        this.rarity = rarity;
        this.mainBlockType = mainBlockType;
    }

    public Rarity getRarity()
    {
        return rarity;
    }

    public Match3Block.BlockType getMainBlockType()
    {
        return mainBlockType;
    }
}
