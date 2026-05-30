package pap.project.game.match3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pap.project.game.match3.model.Match3MoveResult;
import pap.project.game.match3.model.MoveRequest;
import pap.project.game.match3.model.Position;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class Match3BoardTest {

    private Match3Board board;

    @BeforeEach
    public void setUp() {
        board = new Match3Board();
    }

    @Test
    public void test_generated_board_has_allowed_moves() {
        var state = board.getBoardState();
        assertNotNull(state);

        assertFalse(state.allowedMoves().isEmpty(), "Generated board should have allowed moves");
    }

    @Test
    public void test_valid_swap_creates_match_and_counts() throws Exception {
        Match3Block.BlockType a = Match3Block.BlockType.AMETHYST;
        Match3Block.BlockType b = Match3Block.BlockType.CITRINE;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                custom[r][c] = new Match3Block(b);

        custom[1][0] = new Match3Block(a);
        custom[2][0] = new Match3Block(a);
        custom[3][0] = new Match3Block(b);
        custom[3][1] = new Match3Block(a);

        setPrivateField(board, "board", custom);

        setPrivateField(board, "random", new Random(1234L));

        var move = new MoveRequest(new Position(3,0), new Position(3,1));
        Match3MoveResult result = board.makeMove(move);

        assertNotNull(result, "Move should be valid and return a result");
        assertFalse(result.totalMatchedBlocks().isEmpty());
        assertEquals(Integer.valueOf(3), result.totalMatchedBlocks().get(a));
    }

    @Test
    public void test_non_adjacent_swap_is_invalid() {
        var move = new MoveRequest(new Position(0,0), new Position(2,2));
        var result = board.makeMove(move);
        assertNull(result);
    }

    @Test
    public void test_swap_that_does_not_create_match_returns_empty_matches() throws Exception {
        Match3Block.BlockType t1 = Match3Block.BlockType.CITRINE;
        Match3Block.BlockType t2 = Match3Block.BlockType.DIAMOND;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                custom[r][c] = new Match3Block(((r + c) % 2 == 0) ? t1 : t2);
            }
        }

        setPrivateField(board, "board", custom);

        var move = new MoveRequest(new Position(0,0), new Position(0,1));
        var result = board.makeMove(move);
        assertNotNull(result);
        assertTrue(result.totalMatchedBlocks().isEmpty());
    }

    @Test
    public void test_drop_floating_blocks_moves_blocks_down_and_fills_top() throws Exception {
        Match3Block.BlockType a = Match3Block.BlockType.AMETHYST;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                custom[r][c] = new Match3Block(Match3Block.BlockType.EMPTY);

        // Put a single block at row 0, col 0 so it should drop to row 4
        custom[0][0] = new Match3Block(a);
        setPrivateField(board, "board", custom);
        setPrivateField(board, "random", new Random(42L));

        @SuppressWarnings("unchecked")
        java.util.List<MoveRequest> dropped = (java.util.List<MoveRequest>)invokePrivate(board, "dropFloatingBlocks");

        // After dropping, the block originally at (0,0) should be at bottom (4,0)
        Match3Block[][] boardArr = (Match3Block[][]) getPrivateField(board, "board");
        assertEquals(a, boardArr[4][0].getBlockType());
        assertNotEquals(Match3Block.BlockType.EMPTY, boardArr[4][0].getBlockType());

        assertFalse(dropped.isEmpty());
    }

    @Test
    public void test_swap_with_disabled_cell_is_invalid() throws Exception {
        Match3Block.BlockType normal = Match3Block.BlockType.AMETHYST;
        Match3Block.BlockType disabled = Match3Block.BlockType.DISABLED;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                custom[r][c] = new Match3Block(normal);

        custom[0][1] = new Match3Block(disabled);

        setPrivateField(board, "board", custom);

        var move = new MoveRequest(new Position(0,0), new Position(0,1));
        var result = board.makeMove(move);
        assertNull(result, "Swapping into a DISABLED cell must be invalid and return null");
    }

    @Test
    public void test_allowed_moves_do_not_include_disabled_positions() throws Exception {
        Match3Block.BlockType normal = Match3Block.BlockType.RUBY;
        Match3Block.BlockType disabled = Match3Block.BlockType.DISABLED;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                custom[r][c] = new Match3Block(normal);

        int dr = 2, dc = 2;
        custom[dr][dc] = new Match3Block(disabled);

        setPrivateField(board, "board", custom);

        var state = board.getBoardState();
        for (var move : state.allowedMoves()) {
            Position s = move.source();
            Position t = move.target();
            assertFalse((s.row() == dr && s.column() == dc) || (t.row() == dr && t.column() == dc),
                    "Allowed moves must not include DISABLED positions");
        }
    }

    @Test
    public void test_find_matches_detects_three_in_row_shape() throws Exception {
        Match3Block.BlockType a = Match3Block.BlockType.EMERALD;
        Match3Block.BlockType other = Match3Block.BlockType.HEMATITE;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                custom[r][c] = new Match3Block(other);

        custom[1][2] = new Match3Block(a);
        custom[2][2] = new Match3Block(a);
        custom[3][2] = new Match3Block(a);

        setPrivateField(board, "board", custom);

        Object matches = invokePrivate(board, "findMatchedBlocks");
        List<Position> positions = extractPositionsFromMatches(matches);

        assertTrue(containsPosition(positions, 1, 2));
        assertTrue(containsPosition(positions, 2, 2));
        assertTrue(containsPosition(positions, 3, 2));
    }

    @Test
    public void test_find_matches_detects_three_in_column_shape() throws Exception {
        Match3Block.BlockType a = Match3Block.BlockType.RUBY;
        Match3Block.BlockType other = Match3Block.BlockType.CITRINE;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                custom[r][c] = new Match3Block(other);

        custom[2][1] = new Match3Block(a);
        custom[2][2] = new Match3Block(a);
        custom[2][3] = new Match3Block(a);

        setPrivateField(board, "board", custom);

        Object matches = invokePrivate(board, "findMatchedBlocks");
        List<Position> positions = extractPositionsFromMatches(matches);

        assertTrue(containsPosition(positions, 2, 1));
        assertTrue(containsPosition(positions, 2, 2));
        assertTrue(containsPosition(positions, 2, 3));
    }

    @Test
    public void test_find_matches_detects_square_shape() throws Exception {
        Match3Block.BlockType a = Match3Block.BlockType.DIAMOND;
        Match3Block.BlockType other = Match3Block.BlockType.AMETHYST;

        Match3Block[][] custom = new Match3Block[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                custom[r][c] = new Match3Block(other);

        custom[1][1] = new Match3Block(a);
        custom[1][2] = new Match3Block(a);
        custom[2][1] = new Match3Block(a);
        custom[2][2] = new Match3Block(a);

        setPrivateField(board, "board", custom);

        Object matches = invokePrivate(board, "findMatchedBlocks");
        List<Position> positions = extractPositionsFromMatches(matches);

        assertTrue(containsPosition(positions, 1, 1));
        assertTrue(containsPosition(positions, 1, 2));
        assertTrue(containsPosition(positions, 2, 1));
        assertTrue(containsPosition(positions, 2, 2));
    }

    // --- helpers to extract positions from private Matches record ---
    @SuppressWarnings("unchecked")
    private static List<Position> extractPositionsFromMatches(Object matches) throws Exception {
        Method positionsMethod = matches.getClass().getMethod("positions");
        return (List<Position>) positionsMethod.invoke(matches);
    }

    private static boolean containsPosition(List<Position> positions, int row, int col) {
        for (Position p : positions) {
            if (p.row() == row && p.column() == col)
                return true;
        }
        return false;
    }

    // --- helpers for reflection ---
    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Object getPrivateField(Object target, String fieldName) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }

    private static Object invokePrivate(Object target, String methodName, Object... args) throws Exception {
        Method method;
        if (args == null || args.length == 0) {
            method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(target);
        }

        Class<?>[] parameterTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
        method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static void copyBoardInto(Match3Board target, Match3Block[][] source) throws Exception {
        setPrivateField(target, "board", source);
    }
}
