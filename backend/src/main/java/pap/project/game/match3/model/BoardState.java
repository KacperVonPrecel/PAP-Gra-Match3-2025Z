package pap.project.game.match3.model;

import org.springframework.lang.NonNull;
import pap.project.game.match3.Match3Block;

import java.util.List;

/**
 * @param board final state of board after player move.
 * @param allowedMoves list of moves which can be made by player.
 * @param animationSteps list in order of steps (movement by player, destroying of blocks etc.),
 *                       which happen from previous send board to current board.
 *                       In last element it {@link AnimationStep#board} is equal to {@link #board} .
 */
public record BoardState(
        @NonNull Match3Block[][] board,
        @NonNull List<MoveRequest> allowedMoves,
        @NonNull List<AnimationStep> animationSteps
) { }
