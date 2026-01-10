package pap.project.match3.model;

import org.springframework.lang.NonNull;
import pap.project.match3.Match3Block;

public record NewBlock(
        @NonNull Position position,
        @NonNull Match3Block block
) { }
