package pap.project.users.characters;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import pap.project.users.characters.model.CharacterType;
import pap.project.users.User;

import java.util.OptionalLong;

@Entity
@Table(
        name = "userCharacters",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"characterType", "userId"})
        },
        indexes = {
                @Index(name = "index_userCharacters_userId", columnList = "userId")
        }
)
public class UserCharacter
{
    @Id
    @GeneratedValue
    private Long id;

    private CharacterType characterType;

    @Column(name = "userId")
    private long userId;

    /**
     * This is added to tell hibernate to create foreign key.
     * It isn't used anywhere.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    private int level;

    /**
     * Copies count which user actually obtain.
     * It doesn't include copies which was used to level up character.
     */
    private int copiesCount;

    protected UserCharacter() {}

    public UserCharacter(@NonNull CharacterType characterType, long userId, int level, int copiesCount)
    {
        this.characterType = characterType;
        this.userId = userId;
        this.level = level;
        this.copiesCount = copiesCount;
    }

    public @NonNull OptionalLong getId()
    {
        return id != null ? OptionalLong.of(id) : OptionalLong.empty();
    }

    public @NonNull CharacterType getCharacterType()
    {
        return characterType;
    }

    public long getUserId()
    {
        return userId;
    }

    public int getLevel()
    {
        return level;
    }

    public int getCopiesCount()
    {
        return copiesCount;
    }
}
