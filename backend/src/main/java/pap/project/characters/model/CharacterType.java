package pap.project.characters.model;

import org.springframework.lang.NonNull;
import pap.project.characters.Character;
import pap.project.characters.impl.*;
import pap.project.game.match3.Match3Block;

public enum CharacterType
{
    //XXX
    AMETHYST_ENCHANTRESS(Rarity.COMMON, new AmethystEnchantress()),
    TRASH_MAN(Rarity.COMMON, new TrashMan()),
    SACRED_CAT(Rarity.UNCOMMON, new SacredCat()),
    EMERALD_CORE_KNIGHT(Rarity.UNCOMMON, new EmeraldCoreKnight()),
    RUBY_HORNED_DAME(Rarity.RARE, new RubyHornedDame()),
    EXPERIENCED_SWORDMAN(Rarity.RARE, new ExperiencedSwordman()),
    HONEY_TRIGGER(Rarity.RARE, new HoneyTrigger());

    public final Rarity rarity;
    public final Character character;

    CharacterType(@NonNull Rarity rarity, @NonNull Character character)
    {
        this.rarity = rarity;
        this.character = character;
    }

}
