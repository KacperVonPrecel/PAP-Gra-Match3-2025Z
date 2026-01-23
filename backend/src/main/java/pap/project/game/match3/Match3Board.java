package pap.project.game.match3;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.game.match3.model.*;

import java.util.*;

public class Match3Board
{
    private static final int DEFAULT_BOARD_SIZE = 5;

    private record Matches(List<Match3Block> blocks, List<Position> positions) {}

    private final @NonNull Match3Block[][] board;
    private final @NonNull MatchableShape[] matchableShapes;

    private final @NonNull Random random = new Random();

    public Match3Board()
    {
        this.board = generateEmptyBoard();
        this.matchableShapes = MatchableShapeLibrary.ALL_SHAPES;

        generateValidBoard();
    }

    private @NonNull Match3Block[][] generateEmptyBoard()
    {
        final Match3Block[][] blocks = new Match3Block[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE];
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++)
        {
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++)
            {
                blocks[i][j] = new Match3Block();
            }
        }
        return blocks;
    }

    /**
     * @return null if move invalid. Move is consider valid if after moving blocks there is some connection between blocks - XXX check if is valid sentence.
     */
    public @Nullable Match3MoveResult makeMove(@NonNull MoveRequest moveRequest)
    {
        if (!swapBlocks(moveRequest))
            return null;

        final List<AnimationStep> animationSteps = new ArrayList<>();

        MoveRequest swappedBlocks = moveRequest;

        // Destroy, drop and repeat until no matches are left
        Matches matches = findMatchedBlocks();
        final Map<Match3Block.BlockType, Integer> totalMatchedBlocks = new HashMap<>();

        while (!matches.blocks().isEmpty())
        {
            for (Match3Block block : matches.blocks())
                totalMatchedBlocks.compute(block.getBlockType(), (_, v) -> (v != null ? v : 0) + 1);

            destroyBlocks(matches.blocks());

            final List<MoveRequest> dropped = dropFloatingBlocks();
            final List<NewBlock> newBlocks = fillBoard();

            animationSteps.add(new AnimationStep(
                    board,
                    swappedBlocks,
                    matches.positions(),
                    dropped,
                    newBlocks,
                    false
            ));

            swappedBlocks = null; // So only the first step has swappedBlocks
            matches = findMatchedBlocks();
        }

        // Reset the board if there are no more allowed moves
        if (getAllowedMoves().isEmpty())
        {
            generateValidBoard();
            animationSteps.add(new AnimationStep(
                    board,
                    swappedBlocks,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    true
            ));
        }

        final BoardState boardState = new BoardState(
                board,
                getAllowedMoves(),
                animationSteps
        );

        return new Match3MoveResult(boardState, totalMatchedBlocks);
    }

    public @NonNull BoardState getBoardState()
    {
        return new BoardState(
                board,
                getAllowedMoves(),
                new ArrayList<>()
        );
    }

    private void generateValidBoard()
    {
        fillBoard();

        while (!findMatchedBlocks().blocks.isEmpty() || getAllowedMoves().isEmpty())
        {
            clearBoard();
            fillBoard();
        }

    }

    private @NonNull List<NewBlock> fillBoard()
    {
        List<NewBlock> filledPositions = new ArrayList<>();

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                if (board[i][j].getBlockType() == Match3Block.BlockType.EMPTY)
                {
                    Match3Block.BlockType randomType = randomBlockType();
                    board[i][j].setBlockType(randomType);
                    filledPositions.add(new NewBlock(new Position(i, j), new Match3Block(randomType)));
                }
            }
        }

        return filledPositions;
    }

    private @NonNull Matches findMatchedBlocks()
    {
        // TODO: Look into 2D Rabin-Karp because this is awful
        List<Match3Block> matches = new ArrayList<>();
        List<Position> matchPositions = new ArrayList<>();

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                for (MatchableShape shape : matchableShapes)
                {
                    if (!blockMatchesShape(i, j, shape))
                        continue;

                    if (!matches.contains(board[i][j]))
                    {
                        matches.add(board[i][j]);
                        matchPositions.add(new Position(i, j));
                    }

                    for (MatchableShape.RelativeCoordinates relativeCoordinates : shape.relativeCoordinates())
                    {
                        Match3Block block = board[i + relativeCoordinates.x()][j + relativeCoordinates.y()];

                        if (matches.contains(block))
                            continue;

                        matches.add(block);
                        matchPositions.add(new Position(i + relativeCoordinates.x(), j + relativeCoordinates.y()));
                    }
                }
            }
        }

        return new Matches(matches, matchPositions);
    }

    private @NonNull List<MoveRequest> dropFloatingBlocks()
    {
        List<MoveRequest> droppedMoves = new ArrayList<>();

        for (int col = 0; col < board[0].length; col++)
        {
            // Move blocks to bottom per column
            int swap_row = board.length - 1;

            for (int row = board.length - 1; row >= 0; row--)
            {
                if (isBlockOccupied(row, col))
                {
                    board[swap_row][col].setBlockType(board[row][col].getBlockType());

                    if (row !=  swap_row)
                    {
                        droppedMoves.add(new MoveRequest(
                                new Position(row, col),
                                new Position(swap_row, col)
                        ));
                    }

                    swap_row--;
                }
            }

            // Fill the rest with empty
            for (int row = swap_row; row >= 0; row--)
            {
                board[row][col].setBlockType(Match3Block.BlockType.EMPTY);
            }
        }

        return droppedMoves;
    }

    private boolean swapBlocks(@NonNull MoveRequest moveRequest)
    {
        if (!areBlocksSwappable(moveRequest))
            return false;

        forceSwapBlocks(moveRequest);

        return true;
    }

    private void destroyBlocks(@NonNull List<Match3Block> toDestroy)
    {
        for (Match3Block match : toDestroy)
        {
            destroyBlock(match);
        }
    }

    private @NonNull List<MoveRequest> getAllowedMoves()
    {
        final List<MoveRequest> allowedMoves = new ArrayList<>();

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                final List<MoveRequest> movesToCheck = new ArrayList<>();

                if (i > 0)
                    movesToCheck.add(new MoveRequest(new Position(i, j), new Position(i - 1, j)));
                if (i < board.length - 1)
                    movesToCheck.add(new MoveRequest(new Position(i, j), new Position(i + 1, j)));
                if (j > 0)
                    movesToCheck.add(new MoveRequest(new Position(i, j), new Position(i, j - 1)));
                if (j < board.length - 1)
                    movesToCheck.add(new MoveRequest(new Position(i, j), new Position(i, j + 1)));

                for (MoveRequest move : movesToCheck)
                {
                    if (isMoveAllowed(move))
                        allowedMoves.add(move);
                }
            }
        }

        return allowedMoves;
    }

    private void clearBoard()
    {
        for (Match3Block[] row : board)
        {
            for (Match3Block block : row)
                destroyBlock(block);
        }
    }

    private boolean isMoveAllowed(@NonNull MoveRequest move)
    {
        MoveRequest reverse = new MoveRequest(move.target(), move.source());

        forceSwapBlocks(move);
        boolean willMatch = !findMatchedBlocks().blocks().isEmpty();
        forceSwapBlocks(reverse);

        return willMatch;
    }

    private void forceSwapBlocks(@NonNull MoveRequest moveRequest)
    {
        Match3Block temp = board[moveRequest.source().row()][moveRequest.source().column()];

        board[moveRequest.source().row()][moveRequest.source().column()] = board[moveRequest.target().row()][moveRequest.target().column()];
        board[moveRequest.target().row()][moveRequest.target().column()] = temp;
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

        for (MatchableShape.RelativeCoordinates coordinates : shape.relativeCoordinates())
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

        if ((rowDistance == 1 && columnDistance == 0) || (rowDistance == 0 && columnDistance == 1))
            return !getAllowedMoves().isEmpty();

        return false;
    }

    private boolean isBlockOccupied(int row, int column)
    {
        return board[row][column].getBlockType() != Match3Block.BlockType.EMPTY && board[row][column].getBlockType() != Match3Block.BlockType.DISABLED;
    }
}
