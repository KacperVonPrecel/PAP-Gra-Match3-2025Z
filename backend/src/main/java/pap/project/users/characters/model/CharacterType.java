package pap.project.users.characters.model;

public enum CharacterType
{
    AMETHYST_ENCHANTRESS(Rarity.COMMON),
    TRASH_MAN(Rarity.COMMON),
    SACRED_CAT(Rarity.COMMON),
    EMERALD_CORE_KNIGHT(Rarity.UNCOMMON),
    RUBY_HORNED_DAME(Rarity.UNCOMMON),
    EXPERIENCED_SWORDMAN(Rarity.RARE),
    HONEY_TRIGGER(Rarity.RARE)
    ;

    private final Rarity rarity;

    CharacterType(final Rarity rarity)
    {
        this.rarity = rarity;
    }

    public Rarity getRarity()
    {
        return rarity;
    }
}
