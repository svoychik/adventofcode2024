import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class Day6pt2 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        var input = Files.readString(Paths.get(Day6.class.getResource("/day6.txt").toURI()));
        var field = new Day6.Field(input);
        var validObstructionCount = 0;
        var guardInitDirection = field.guardDirection;
        var guardInitPosition = field.guardPos;
        for (int y = 0; y < field.rows; y++) {
            for (int x = 0; x < field.cols; x++) {
                var position = new Day6.Position(x, y);
//                System.out.println("New position: " + position);
                
                if (position.equals(field.guardPos) || field.obstacles.contains(position)) {
                    continue;
                }
                // Temporarily place an obstruction
                field.obstacles.add(position);

                // Check if placing the obstruction causes a loop
                if (causesLoop(field)) {
                    
                    validObstructionCount++;
                    System.out.println("Curr number of obstructions " + validObstructionCount);
                    
                }

                // Remove the obstruction
                field.guardPos = guardInitPosition;
                field.guardDirection = guardInitDirection;
                field.obstacles.remove(position);
            }
        }

        System.out.println("Number of positions causing a loop: " + validObstructionCount);
    }

    public static boolean causesLoop(Day6.Field field) {
        // remember that there maybe a situation when guard visited specific position but has a different direction
        var visited = new HashSet<String>();
        while (Day6.IsInTheFiled(field.guardPos, field)) {
            var nextPosition = Day6.calculateNextPosition(field);
            if (field.obstacles.contains(nextPosition)) {
                field.guardDirection = switch (field.guardDirection) {
                    case Up -> Day6.Direction.Right;
                    case Right -> Day6.Direction.Down;
                    case Left -> Day6.Direction.Up;
                    case Down -> Day6.Direction.Left;
                };
            }
            nextPosition = Day6.calculateNextPosition(field);
            field.guardPos = nextPosition;
            var state = getUniqueState(field.guardPos, field.guardDirection);
            if(visited.contains(state)) {
                return true;
            }
            visited.add(state);
        }
        return false;
    }
    
    private static String getUniqueState(Day6.Position guardPosition, Day6.Direction direction) {
        return String.format("%s#%s#%s", guardPosition.x(), guardPosition.y(), direction.name());
    }
}
