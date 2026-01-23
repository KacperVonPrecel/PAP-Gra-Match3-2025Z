package pap.project.game.match3.model;

import org.springframework.lang.NonNull;
import pap.project.game.match3.Match3Block;

public record NewBlock(
        @NonNull Position position,
        @NonNull Match3Block block
) { }
