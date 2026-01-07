package pap.project.match3.model;

import pap.project.match3.Match3Block;

public record AnimationStep(
        Match3Block[][] board,
        MoveRequest swapped,
        Position[] destroyed,
        MoveRequest[] falling,
        NewBlock[] newBlocks
) { }
