package pap.project.game.match3.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import pap.project.game.match3.Match3Block;

import java.util.List;

public record AnimationStep(
        @NonNull Match3Block[][] board,
        @Nullable MoveRequest swapped,
        @NonNull List<Position> destroyed,
        @NonNull List<MoveRequest> falling,
        @NonNull List<NewBlock> newBlocks,
        boolean resetBoard
) { }
