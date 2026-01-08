package pap.project.match3.model;

import pap.project.match3.Match3Block;

import java.util.List;

public record AnimationStep(
        Match3Block[][] board,
        MoveRequest swapped,
        List<Position> destroyed,
        List<MoveRequest> falling,
        List<NewBlock> newBlocks
) { }
