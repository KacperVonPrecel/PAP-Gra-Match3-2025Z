package pap.project.game.match3;

public class MatchableShapeLibrary
{
    public static final MatchableShape THREE_IN_ROW = new MatchableShape(
            new MatchableShape.RelativeCoordinates[] {
                    new MatchableShape.RelativeCoordinates(1, 0),
                    new MatchableShape.RelativeCoordinates(2, 0)
            }
    );

    public static final MatchableShape THREE_IN_COLUMN = new MatchableShape(
            new MatchableShape.RelativeCoordinates[] {
                    new MatchableShape.RelativeCoordinates(0, 1),
                    new MatchableShape.RelativeCoordinates(0, 2)
            }
    );

    public static final MatchableShape SQUARE = new MatchableShape(
            new MatchableShape.RelativeCoordinates[] {
                    new MatchableShape.RelativeCoordinates(1, 0),
                    new MatchableShape.RelativeCoordinates(0, 1),
                    new MatchableShape.RelativeCoordinates(1, 1),
            }
    );

    public static final MatchableShape[] ALL_SHAPES = new MatchableShape[] {
            THREE_IN_ROW,
            THREE_IN_COLUMN,
            SQUARE
    };
}
