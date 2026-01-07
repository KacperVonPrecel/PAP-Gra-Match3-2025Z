package pap.project.match3;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match3Board
{
    private final @NonNull Match3Block[][] board;
    private final @NonNull MatchableShape[] matchableShapes;

    private final @NonNull Random random = new Random();

    public record MoveRequest(int sourceRow, int sourceColumn, int targetRow, int targetColumn) { };

    public Match3Board(@NonNull Match3Block[][] board, @NonNull MatchableShape[] matchableShapes)
    {
        this.board = board;
        this.matchableShapes = matchableShapes;

        fillBoard();
    }

    public @NonNull Match3Block[][] getBlocks()
    {
        return board;
    }

    public void fillBoard()
    {
        for (Match3Block[] row : board)
        {
            for (Match3Block block : row)
            {
                if (block.getBlockType() == Match3Block.BlockType.EMPTY)
                    block.setBlockType(randomBlockType());
            }
        }
    }

    public void dropFloatingBlocks()
    {
        // TODO: There may also be a better algorithm for this
        for (int i = board.length - 1; i >= 0; i--)
        {
            for (int j = board[i].length - 1; j >= 0; j--)
            {
                if (board[i][j].getBlockType() != Match3Block.BlockType.EMPTY)
                    continue;

                int above = 1;
                while (!isOutOfBounds(i - above, j))
                {
                    if (board[i - above][j].getBlockType() != Match3Block.BlockType.EMPTY)
                    {
                        MoveRequest moveRequest = new MoveRequest(i, j, i - above, j);
                        forceSwapBlocks(moveRequest);

                        break;
                    }

                    above++;
                }
            }
        }
    }

    public boolean swapBlocks(@NonNull MoveRequest moveRequest)
    {
        // TODO: Add check if swap causes matches
        if (!areBlocksSwappable(moveRequest))
            return false;

        forceSwapBlocks(moveRequest);

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

    private void forceSwapBlocks(@NonNull MoveRequest moveRequest)
    {
        Match3Block temp = board[moveRequest.sourceRow()][moveRequest.sourceColumn()];

        board[moveRequest.sourceRow()][moveRequest.sourceColumn()] = board[moveRequest.targetRow()][moveRequest.targetColumn()];
        board[moveRequest.targetRow()][moveRequest.targetColumn()] = temp;
    }

    private @NonNull List<Match3Block> findMatchedBlocks()
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

    private void destroyBlock(@NonNull Match3Block block)
    {
        block.setBlockType(Match3Block.BlockType.EMPTY);
    }

    private Match3Block.BlockType randomBlockType()
    {
        int randomBlockTypeValue = random.nextInt(Match3Block.BlockType.EMPTY.ordinal() + 1, Match3Block.BlockType.values().length); // Start from 1 so it ignores BlockType.None
        return Match3Block.BlockType.values()[randomBlockTypeValue];
    }

    private boolean blockMatchesShape(int row, int column, @NonNull MatchableShape shape)
    {
        if (isOutOfBounds(row, column))
            return false;

        Match3Block.BlockType thisBlockType = board[row][column].getBlockType();

        if  (thisBlockType == Match3Block.BlockType.DISABLED || thisBlockType == Match3Block.BlockType.EMPTY)
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
        return row < 0 || row >= board.length || column < 0 || column >= board[0].length || board[row][column].getBlockType() == Match3Block.BlockType.DISABLED;
    }

    private boolean areBlocksSwappable(@NonNull MoveRequest moveRequest)
    {
        if (isOutOfBounds(moveRequest.sourceRow(), moveRequest.sourceColumn()) || isOutOfBounds(moveRequest.targetRow(), moveRequest.targetColumn()))
            return false;

        int rowDistance = Math.abs(moveRequest.targetRow() - moveRequest.sourceRow());
        int columnDistance = Math.abs(moveRequest.targetColumn() - moveRequest.sourceColumn());

        return (rowDistance == 1 && columnDistance == 0) || (rowDistance == 0 && columnDistance == 1);
    }
}
