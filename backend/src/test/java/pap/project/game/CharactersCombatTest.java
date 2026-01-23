package pap.project.game;

import org.junit.jupiter.api.Test;
import pap.project.game.match3.Match3Block;
import pap.project.game.model.CharacterCombatResult;
import pap.project.game_history.model.HistoryCharacterData;
import pap.project.characters.CharacterInGame;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class CharactersCombatTest {

    private static class LocalTestCharacter extends CharacterInGame {
        private final Function<Map<Match3Block.BlockType, Integer>, Integer> damageFunc;

        public LocalTestCharacter(long characterId, int maxHealth, Function<Map<Match3Block.BlockType, Integer>, Integer> damageFunc) {
            super(characterId, maxHealth);
            this.damageFunc = damageFunc;
        }

        @Override
        public int calculateDamage(Map<Match3Block.BlockType, Integer> totalMatchedBlocks) {
            return damageFunc.apply(totalMatchedBlocks);
        }

        @Override
        public HistoryCharacterData historyCharacterData() {
            return new HistoryCharacterData(null, 1);
        }
    }

    @Test
    public void test_selects_highest_damage_attacker_and_applies_damage() {
        LocalTestCharacter attacker = new LocalTestCharacter(1L, 10, m -> 2);
        LocalTestCharacter attackerHigh = new LocalTestCharacter(2L, 10, m -> 5);
        LocalTestCharacter defender = new LocalTestCharacter(3L, 6, m -> 0);

        CharactersCombat combat = new CharactersCombat(List.of(attackerHigh, attacker), List.of(defender));

        Map<Match3Block.BlockType, Integer> totals = Map.of(Match3Block.BlockType.RUBY, 3);

        CharacterCombatResult result = combat.process(true, totals);

        assertEquals(2, result.firstPlayerCharacterState().charactersHealth().size());

        // defender should have taken damage 5 -> from 6 to 1
        assertEquals(1, result.secondPlayerCharacterState().charactersHealth().get(defender.characterId));
        assertEquals(attackerHigh.characterId, result.attackingCharacterId());
        assertFalse(result.gameEnded());
    }

    @Test
    public void test_single_defender_dies_game_ended_true() {
        LocalTestCharacter attacker = new LocalTestCharacter(1L, 10, m -> 5);
        LocalTestCharacter defender = new LocalTestCharacter(3L, 5, m -> 0);

        CharactersCombat combat = new CharactersCombat(List.of(attacker), List.of(defender));

        CharacterCombatResult result = combat.process(true, Map.of());

        assertTrue(result.gameEnded());
    }

    @Test
    public void test_tieDamage_selectsOne_and_appliesDamage() {
        LocalTestCharacter attacker1 = new LocalTestCharacter(1L, 10, m -> 3);
        LocalTestCharacter attacker2 = new LocalTestCharacter(2L, 10, m -> 3);
        LocalTestCharacter defender = new LocalTestCharacter(3L, 10, m -> 0);

        CharactersCombat combat = new CharactersCombat(List.of(attacker1, attacker2), List.of(defender));

        Map<Match3Block.BlockType, Integer> totals = Map.of(Match3Block.BlockType.RUBY, 1);

        CharacterCombatResult result = combat.process(true, totals);

        // defender should have taken damage 3 -> from 10 to 7
        assertEquals(7, result.secondPlayerCharacterState().charactersHealth().get(defender.characterId));
        long chosen = result.attackingCharacterId();
        assertTrue(chosen == attacker1.characterId || chosen == attacker2.characterId);
        assertFalse(result.gameEnded());
    }
}
