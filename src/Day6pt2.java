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

        for (int y = 0; y < field.rows; y++) {
            for (int x = 0; x < field.cols; x++) {
                var position = new Day6.Position(x, y);

                if (position.equals(field.guardPos) || field.obstacles.contains(position)) {
                    continue;
                }

                // Temporarily place an obstruction
                field.obstacles.add(position);

                // Check if placing the obstruction causes a loop
                if (causesLoop(field)) {
                    validObstructionCount++;
                }

                // Remove the obstruction
                field.obstacles.remove(position);
            }
        }

        System.out.println("Number of positions causing a loop: " + validObstructionCount);
    }

    public static boolean causesLoop(Day6.Field field) {
        var visited = new HashSet<Day6.Position>();
        while (Day6.IsInTheFiled(field.guardPos, field)) {
            var nextPosition = Day6.calculateNextPosition(field);
            if (field.obstacles.contains(nextPosition)) {
                var nextDirection = switch (field.guardDirection) {
                    case Up -> Day6.GuardDirection.Right;
                    case Right -> Day6.GuardDirection.Down;
                    case Left -> Day6.GuardDirection.Up;
                    case Down -> Day6.GuardDirection.Left;
                };
                field.guardDirection = nextDirection;
            }
            nextPosition = Day6.calculateNextPosition(field);
            System.out.println("New guard position: " + nextPosition);
            field.guardPos = nextPosition;
            if(visited.contains(field.guardPos)) {
                return true;
            }
            visited.add(nextPosition);
        }

        return false; // No loop detected
    }


}
