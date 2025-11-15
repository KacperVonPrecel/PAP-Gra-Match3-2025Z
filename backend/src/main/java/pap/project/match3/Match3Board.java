package pap.project.match3;

import java.util.Random;

public class Match3Board
{
    private final Match3Block[][] board;
    private final MatchableShape[] matchableShapes;

    private final Random random = new Random();

    public Match3Board(Match3Block[][] board, MatchableShape[] matchableShapes)
    {
        this.board = board;
        this.matchableShapes = matchableShapes;

        fillInBlocks();
    }

    public Match3Block[][] getBoard()
    {
        return board;
    }

    public void swapBlocks(int source_row, int source_col, int target_row, int target_col)
    {
        Match3Block temp = board[source_row][source_col];

        board[source_row][source_col] = board[target_row][target_col];
        board[target_row][target_col] = temp;
    }

    public void destroyBlock(int row, int col)
    {
        board[row][col].setBlockType(Match3Block.BlockType.None);
    }

    public Match3Block[] findMatchedBlocks()
    {
        // TODO: Look into 2D Rabin-Karp
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroyMatchedBlocks()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fillInBlocks()
    {
        for (Match3Block[] row : board)
        {
            for (Match3Block block : row)
            {
                if (block.getBlockType() == Match3Block.BlockType.None)
                    block.setBlockType(randomBlockType());
            }
        }
    }

    private Match3Block.BlockType randomBlockType()
    {
        int randomBlockTypeValue = random.nextInt(1, Match3Block.BlockType.values().length); // Start from 1 so it ignores BlockType.None
        return Match3Block.BlockType.values()[randomBlockTypeValue];
    }
}
