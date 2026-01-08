package pap.project.match3.model;

import pap.project.match3.Match3Block;

import java.util.List;

public record BoardState(
        Match3Block[][] board,
        List<MoveRequest> allowedMoves,
        List<AnimationStep> animationSteps
) { }
