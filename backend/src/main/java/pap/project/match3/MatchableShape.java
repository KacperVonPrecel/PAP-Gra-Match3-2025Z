package pap.project.match3;

import org.springframework.lang.NonNull;

public class MatchableShape {
    @NonNull private final RelativeCoordinates[] relativeCoordinates;

    public record RelativeCoordinates(int x, int y) { }

    public MatchableShape(RelativeCoordinates[] relativeCoordinates)
    {
        this.relativeCoordinates = relativeCoordinates;
    }

    public RelativeCoordinates[] getRelativeCoordinates()
    {
        return relativeCoordinates;
    }
}
