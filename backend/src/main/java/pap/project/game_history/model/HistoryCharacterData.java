package pap.project.game_history.model;

import pap.project.characters.model.CharacterType;

import java.io.Serializable;

public class HistoryCharacterData implements Serializable {

    private CharacterType characterType;
    private int level;

    protected HistoryCharacterData() {
    }

    public HistoryCharacterData(CharacterType characterType, int level) {
        this.characterType = characterType;
        this.level = level;
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public int getLevel() {
        return level;
    }

    public void setCharacterType(CharacterType characterType) {
        this.characterType = characterType;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
