package pap.project.match3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match3Board
{
    private final Match3Block[][] board;
    private final MatchableShape[] matchableShapes;

    private final Random random = new Random();

    public record MoveRequest(int sourceRow, int sourceColumn, int targetRow, int targetColumn) { };

    public Match3Board(Match3Block[][] board, MatchableShape[] matchableShapes)
    {
        this.board = board;
        this.matchableShapes = matchableShapes;

        fillInBlocks();
    }

    public Match3Block[][] getBlocks()
    {
        return board;
    }

    public boolean swapBlocks(MoveRequest moveRequest)
    {
        // TODO: Add check if swap causes matches
        if (!areBlocksSwappable(moveRequest))
            return false;

        Match3Block temp = board[moveRequest.sourceRow()][moveRequest.sourceColumn()];

        board[moveRequest.sourceRow()][moveRequest.sourceColumn()] = board[moveRequest.targetRow()][moveRequest.targetColumn()];
        board[moveRequest.targetRow()][moveRequest.targetColumn()] = temp;

        return true;
    }

    public void destroyMatchedBlocks()
    {
        List<Match3Block> matches = findMatchedBlocks();

        for (Match3Block match : matches)
        {
            destroyBlock(match);
        }
    }

    // TODO: Distinction between randomize and fill in (the latter should make blocks fall)
    public void fillInBlocks()
    {
        for (Match3Block[] row : board)
        {
            for (Match3Block block : row)
            {
                if (block.getBlockType() == Match3Block.BlockType.Empty)
                    block.setBlockType(randomBlockType());
            }
        }
    }

    private List<Match3Block> findMatchedBlocks()
    {
        // TODO: Look into 2D Rabin-Karp because this is awful
        List<Match3Block> matches = new ArrayList<>();

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                for (MatchableShape shape : matchableShapes)
                {
                    if (!blockMatchesShape(i, j, shape))
                        continue;

                    if (!matches.contains(board[i][j]))
                        matches.add(board[i][j]);

                    for (MatchableShape.RelativeCoordinates relativeCoordinates : shape.getRelativeCoordinates())
                    {
                        Match3Block block = board[i + relativeCoordinates.x()][j + relativeCoordinates.y()];

                        if (matches.contains(block))
                            continue;

                        matches.add(block);
                    }
                }
            }
        }

        return matches;
    }

    private void destroyBlock(Match3Block block)
    {
        block.setBlockType(Match3Block.BlockType.Empty);
    }

    private Match3Block.BlockType randomBlockType()
    {
        int randomBlockTypeValue = random.nextInt(Match3Block.BlockType.Empty.ordinal() + 1, Match3Block.BlockType.values().length); // Start from 1 so it ignores BlockType.None
        return Match3Block.BlockType.values()[randomBlockTypeValue];
    }

    private boolean blockMatchesShape(int row, int column, MatchableShape shape)
    {
        if (isOutOfBounds(row, column))
            return false;

        Match3Block.BlockType thisBlockType = board[row][column].getBlockType();

        if  (thisBlockType == Match3Block.BlockType.Disabled || thisBlockType == Match3Block.BlockType.Empty)
            return false;

        for (MatchableShape.RelativeCoordinates coordinates : shape.getRelativeCoordinates())
        {
            if (isOutOfBounds(row + coordinates.x(), column + coordinates.y()))
                return false;

            if (!(board[row +  coordinates.x()][column + coordinates.y()].getBlockType() == thisBlockType))
                return false;
        }

        return true;
    }

    private boolean isOutOfBounds(int row, int column)
    {
        return row < 0 || row >= board.length || column < 0 || column >= board[0].length || board[row][column].getBlockType() == Match3Block.BlockType.Disabled;
    }

    private boolean areBlocksSwappable(MoveRequest moveRequest)
    {
        if (isOutOfBounds(moveRequest.sourceRow(), moveRequest.sourceColumn()) || isOutOfBounds(moveRequest.targetRow(), moveRequest.targetColumn()))
            return false;

        int rowDistance = Math.abs(moveRequest.targetRow() - moveRequest.sourceRow());
        int columnDistance = Math.abs(moveRequest.targetColumn() - moveRequest.sourceColumn());

        return (rowDistance == 1 && columnDistance == 0) || (rowDistance == 0 && columnDistance == 1);
    }
}
