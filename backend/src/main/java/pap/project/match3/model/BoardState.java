package pap.project.match3.model;

import org.springframework.lang.NonNull;
import pap.project.match3.Match3Block;

import java.util.List;

public record BoardState(
        @NonNull Match3Block[][] board,
        @NonNull List<MoveRequest> allowedMoves,
        @NonNull List<AnimationStep> animationSteps
) { }
