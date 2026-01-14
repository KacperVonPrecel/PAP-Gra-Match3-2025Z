package pap.project.characters.model;

import org.springframework.lang.NonNull;
import pap.project.characters.Character;
import pap.project.characters.impl.AmethystEnchantress;
import pap.project.game.match3.Match3Block;

public enum CharacterType
{
    //XXX
    AMETHYST_ENCHANTRESS(Rarity.COMMON, new AmethystEnchantress()),
    TRASH_MAN(Rarity.COMMON, new AmethystEnchantress()),
    SACRED_CAT(Rarity.UNCOMMON, new AmethystEnchantress()),
    EMERALD_CORE_KNIGHT(Rarity.UNCOMMON, new AmethystEnchantress()),
    RUBY_HORNED_DAME(Rarity.RARE, new AmethystEnchantress()),
    EXPERIENCED_SWORDMAN(Rarity.RARE, new AmethystEnchantress()),
    HONEY_TRIGGER(Rarity.RARE, new AmethystEnchantress());

    public final Rarity rarity;
    public final Character character;

    CharacterType(@NonNull Rarity rarity, @NonNull Character character)
    {
        this.rarity = rarity;
        this.character = character;
    }

}
