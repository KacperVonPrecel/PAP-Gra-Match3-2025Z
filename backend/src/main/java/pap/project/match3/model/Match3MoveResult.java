package pap.project.match3.model;


import org.springframework.lang.NonNull;
import pap.project.match3.Match3Block;

import java.util.Map;

public record Match3MoveResult(
        @NonNull BoardState newBoardState,
        @NonNull Map<Match3Block.BlockType, Integer> totalMatchedBlocks
) {
}
