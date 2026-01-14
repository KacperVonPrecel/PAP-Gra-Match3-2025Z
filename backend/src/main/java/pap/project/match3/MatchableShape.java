package pap.project.match3;

import org.springframework.lang.NonNull;

//XXX remove it if only is required RelativeCoordinates
public record MatchableShape(@NonNull RelativeCoordinates[] relativeCoordinates) {
    public record RelativeCoordinates(int x, int y) { }
}
