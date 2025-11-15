package pap.project.match3;

import org.springframework.lang.NonNull;

public class Match3Block
{
    @NonNull private BlockType blockType = BlockType.None;

    public enum BlockType
    {
        None,
        Red,
        Green,
        Blue,
        Yellow,
    }

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
