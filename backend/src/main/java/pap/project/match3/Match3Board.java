package pap.project.match3;

import org.springframework.lang.NonNull;
import pap.project.match3.model.MoveRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Match3Board
{
    private final @NonNull Match3Block[][] board;
    private final @NonNull MatchableShape[] matchableShapes;

    private final @NonNull Random random = new Random();

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
        for (int col = 0; col < board[0].length; col++)
        {
            // Move blocks to bottom per column
            int swap_row = board.length - 1;

            for (int row = board.length - 1; row >= 0; row--)
            {
                if (isBlockOccupied(row, col))
                {
                    board[swap_row][col].setBlockType(board[row][col].getBlockType());
                    swap_row--;
                }
            }

            // Fill the rest with empty
            for (int row = swap_row; row >= 0; row--)
            {
                board[row][col].setBlockType(Match3Block.BlockType.EMPTY);
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
        Match3Block temp = board[moveRequest.source().row()][moveRequest.source().column()];

        board[moveRequest.source().row()][moveRequest.source().column()] = board[moveRequest.target().row()][moveRequest.target().column()];
        board[moveRequest.target().row()][moveRequest.target().column()] = temp;
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
        if (isOutOfBounds(moveRequest.source().row(), moveRequest.source().column()) || isOutOfBounds(moveRequest.target().row(), moveRequest.target().column()))
            return false;

        int rowDistance = Math.abs(moveRequest.target().row() - moveRequest.source().row());
        int columnDistance = Math.abs(moveRequest.target().column() - moveRequest.source().column());

        return (rowDistance == 1 && columnDistance == 0) || (rowDistance == 0 && columnDistance == 1);
    }

    private boolean isBlockOccupied(int row, int column)
    {
        return board[row][column].getBlockType() != Match3Block.BlockType.EMPTY && board[row][column].getBlockType() != Match3Block.BlockType.DISABLED;
    }
}
