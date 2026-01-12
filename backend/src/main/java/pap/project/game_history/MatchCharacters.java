package pap.project.game_history;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pap.project.game_history.model.HistoryCharacterData;
import pap.project.users.characters.UserCharacter;

import java.util.List;

@Entity
@Table (
        name = "match_characters"
)
public class MatchCharacters {

    @Id
    @Column (name = "match_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn (name = "match_id")
    private Match match;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "winner_first_character_record")
    private HistoryCharacterData winnerFirstCharacterRecord;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "winner_second_character_record")
    private HistoryCharacterData winnerSecondCharacterRecord;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "winner_third_character_record")
    private HistoryCharacterData winnerThirdCharacterRecord;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "loser_first_character_record")
    private HistoryCharacterData loserFirstCharacterRecord;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "loser_second_character_record")
    private HistoryCharacterData loserSecondCharacterRecord;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "loser_third_character_record")
    private HistoryCharacterData loserThirdCharacterRecord;

    protected MatchCharacters() {}

    public MatchCharacters(Match match, List<HistoryCharacterData> winnerCharacters, List<HistoryCharacterData> loserCharacters) {
        this.match = match;
        this.winnerFirstCharacterRecord = winnerCharacters.get(0);
        this.winnerSecondCharacterRecord = winnerCharacters.get(1);
        this.winnerThirdCharacterRecord = winnerCharacters.get(2);
        this.loserFirstCharacterRecord = loserCharacters.get(0);
        this.loserSecondCharacterRecord = loserCharacters.get(1);
        this.loserThirdCharacterRecord = loserCharacters.get(2);
    }

    public HistoryCharacterData getWinnerFirstCharacterRecord() {
        return winnerFirstCharacterRecord;
    }

    public HistoryCharacterData getWinnerSecondCharacterRecord() {
        return winnerSecondCharacterRecord;
    }

    public HistoryCharacterData getWinnerThirdCharacterRecord() {
        return winnerThirdCharacterRecord;
    }

    public HistoryCharacterData getLoserFirstCharacterRecord() {
        return loserFirstCharacterRecord;
    }

    public HistoryCharacterData getLoserSecondCharacterRecord() {
        return loserSecondCharacterRecord;
    }

    public HistoryCharacterData getLoserThirdCharacterRecord() {
        return loserThirdCharacterRecord;
    }
}
