package pap.project.match3;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.NonNull;

public class Match3Block
{
    public enum BlockType
    {
        DISABLED,
        EMPTY,
        AMETHYST,
        CITRINE,
        DIAMOND,
        EMERALD,
        HEMATITE,
        RUBY
    }

    private @NonNull BlockType blockType = BlockType.EMPTY;

    public Match3Block() { }
    public Match3Block(@NonNull BlockType blockType)
    {
        this.blockType = blockType;
    }

    public @NonNull BlockType getBlockType()
    {
        return blockType;
    }

    public void setBlockType(@NonNull BlockType blockType)
    {
        this.blockType = blockType;
    }
}
