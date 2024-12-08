import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;


public class Day6 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        var input = Files.readString(Paths.get(Day5v2.class.getResource("/day6.txt").toURI()));
        var field = new Field(input);
        var visited = new HashSet<Position>();
        while(IsInTheFiled(field.guardPos, field)) {
            var nextPosition = calculateNextPosition(field);
            if(field.obstacles.contains(nextPosition)) {
                System.out.println("Found obstacle at " + nextPosition);
                System.out.println("Current direction " + field.guardDirection);
                var nextDirection = switch (field.guardDirection) {
                    case Up -> GuardDirection.Right;
                    case Right -> GuardDirection.Down;
                    case Left -> GuardDirection.Up;
                    case Down -> GuardDirection.Left;
                };
                field.guardDirection = nextDirection;

                System.out.println("Direction changed: " + field.guardDirection);
            }
            nextPosition = calculateNextPosition(field);
            System.out.println("New guard position: " + nextPosition);
            field.guardPos = nextPosition;
            visited.add(nextPosition);
        };
        
        // -1 to remove last position that is out of boundaries 
        System.out.println("Guard has visited " + (visited.stream().count() - 1) + " distinct postions");
    }
    
    public static Position calculateNextPosition(Field field) {
        var nextPosition = switch (field.guardDirection) {
            case Up:
                yield new Position(field.guardPos.x, field.guardPos.y - 1);
            case Down:
                yield new Position(field.guardPos.x, field.guardPos.y + 1);
            case Left:
                yield new Position(field.guardPos.x - 1, field.guardPos.y);
            case Right:
                yield new Position(field.guardPos.x + 1, field.guardPos.y);
        };
        return nextPosition;
    }
    
    public static boolean IsInTheFiled(Position guardPos, Field field) {
        if(guardPos.x < 0 || guardPos.y < 0)
            return false;
        //0-based index
        if(guardPos.x >= field.cols || guardPos.y >= field.rows)
            return false;
        return true;
    }
    
    public record Position (int x, int y) {};
    public enum GuardDirection { Up, Right, Left, Down }
    public static class Field
    {
        public int rows;
        public int cols;
        public Position guardPos;
        public GuardDirection guardDirection;
        public final HashSet<Position> obstacles = new HashSet<>();
        public Field(String input)
        {
            var lines = input.split("\n");
            rows = lines.length;
            cols = lines[0].length();
            // i is x (col); j is y (row)
            for (int i = 0; i < lines.length; i++) {
                var line = lines[i];
                for (var j = 0; j < line.length(); j++) {
                    var currChar = line.charAt(j);
                    if (currChar == '#') {
                        var position = new Position(j, i);
                        obstacles.add(position);
                        System.out.println("Obstacle #(" + i + "," + j + "): "  + position);
                    }
                    else if (currChar == '^') {
                        guardDirection = GuardDirection.Up;
                        guardPos = new Position(j, i);
                        System.out.println("Guard #(" + i + "," + j + "): "  + guardPos);
                    }
                }
            }
        }
    }
}
