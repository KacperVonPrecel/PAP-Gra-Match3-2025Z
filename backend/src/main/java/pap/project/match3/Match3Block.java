package pap.project.match3;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.NonNull;

public class Match3Block
{
    private @NonNull BlockType blockType = BlockType.EMPTY;

    // TODO: Block type for disabled block (different board shapes support)
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
