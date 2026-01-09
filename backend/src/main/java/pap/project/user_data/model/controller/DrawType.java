package pap.project.user_data.model.controller;

import pap.project.users.characters.model.Rarity;

import java.util.Map;

public enum DrawType
{
    COMMON(Map.of(
            Rarity.COMMON, 90,
            Rarity.UNCOMMON, 10,
            Rarity.RARE, 0
    )),
    UNCOMMON(Map.of(
            Rarity.COMMON, 50,
            Rarity.UNCOMMON, 45,
            Rarity.RARE, 5
    )),
    RARE(Map.of(
            Rarity.COMMON, 30,
            Rarity.UNCOMMON, 40,
            Rarity.RARE, 30
    ));

    private final Map<Rarity, Integer> dropRates;

    DrawType(final Map<Rarity, Integer> dropRates)
    {
        this.dropRates = dropRates;
    }

    public Map<Rarity, Integer> getDropRates()
    {
        return dropRates;
    }
}
