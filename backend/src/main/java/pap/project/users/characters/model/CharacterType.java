package pap.project.users.characters.model;

public enum CharacterType
{
    // XXX
    AMETHYST_ENCHANTRESS(Rarity.COMMON),
    TRASH_MAN(Rarity.COMMON),
    SACRED_CAT(Rarity.UNCOMMON),
    EMERALD_CORE_KNIGHT(Rarity.UNCOMMON),
    RUBY_HORNED_DAME(Rarity.RARE);

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
