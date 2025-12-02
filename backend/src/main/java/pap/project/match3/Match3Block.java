package pap.project.match3;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.NonNull;

public class Match3Block
{
    @NonNull private BlockType blockType = BlockType.Empty;

    // TODO: Block type for disabled block (different board shapes support)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public enum BlockType
    {
        Disabled,
        Empty,
        Red,
        Green,
        Blue,
        Yellow,
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
