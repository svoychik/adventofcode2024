import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {
    private static final int GRID_WIDTH = 101;
    private static final int GRID_HEIGHT = 103;
    private static final int PART1_ITERATIONS = 100;
    private static final int PART2_MAX_STEPS = 10000;

    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day14.class.getResource("/day14.txt").toURI()));
        List<Robot> robots = parseInput(input);

        int part1Answer = solvePart1(robots);
        System.out.println("Part 1 Answer: " + part1Answer);

        int part2Answer = solvePart2(robots);
        System.out.println("Part 2 Answer: " + part2Answer);
    }

    private static int solvePart1(List<Robot> robots) {
        var updatedRobots = deepCopyRobots(robots);
        for (int i = 0; i < PART1_ITERATIONS; i++) {
            updatedRobots = moveRobots(updatedRobots);
        }
        return calculateQuadrants(updatedRobots);
    }

    private static int solvePart2(List<Robot> robots) {
        List<Robot> updatedRobots = deepCopyRobots(robots);
        for (int step = 1; step <= PART2_MAX_STEPS; step++) {
            updatedRobots = moveRobots(updatedRobots);
            if (areAllRobotsSeparate(updatedRobots)) {
                return step;
            }
        }
        return 0; // if no solution is found
    }

    private static List<Robot> parseInput(String input) {
        List<Robot> robots = new ArrayList<>();
        Pattern pattern = Pattern.compile("-?\\d+");
        for (String line : input.split("\\n")) {
            Matcher matcher = pattern.matcher(line);
            int[] robotData = new int[4];
            int index = 0;
            while (matcher.find()) {
                robotData[index++] = Integer.parseInt(matcher.group());
            }
            robots.add(new Robot(robotData[0], robotData[1], robotData[2], robotData[3]));
        }
        return robots;
    }

    private static List<Robot> moveRobots(List<Robot> robots) {
        return robots.stream()
                .map(robot -> {
                    int x = (robot.x() + robot.vx()) % GRID_WIDTH;
                    int y = (robot.y() + robot.vy()) % GRID_HEIGHT;
                    x = (x < 0) ? x + GRID_WIDTH : x;
                    y = (y < 0) ? y + GRID_HEIGHT : y;
                    return new Robot(x, y, robot.vx(), robot.vy());
                })
                .toList();
    }

    private static int calculateQuadrants(List<Robot> robots) {
        int[] quadrantCounts = new int[4];
        for (Robot robot : robots) {
            if (robot.x < GRID_WIDTH / 2 && robot.y < GRID_HEIGHT / 2) {
                quadrantCounts[0]++;
            } else if (robot.x >= GRID_WIDTH / 2 && robot.y < GRID_HEIGHT / 2) {
                quadrantCounts[1]++;
            } else if (robot.x < GRID_WIDTH / 2 && robot.y >= GRID_HEIGHT / 2) {
                quadrantCounts[2]++;
            } else {
                quadrantCounts[3]++;
            }
        }
        return quadrantCounts[0] * quadrantCounts[1] * quadrantCounts[2] * quadrantCounts[3];
    }

    private static boolean areAllRobotsSeparate(List<Robot> robots) {
        for (int i = 0; i < robots.size(); i++) {
            Robot r1 = robots.get(i);
            for (int j = i + 1; j < robots.size(); j++) {
                Robot r2 = robots.get(j);
                if (r1.x == r2.x && r1.y == r2.y) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<Robot> deepCopyRobots(List<Robot> robots) {
        return robots.stream()
                .map(robot -> new Robot(robot.x(), robot.y(), robot.vx(), robot.vy()))
                .toList();
    }

    private record Robot(int x, int y, int vx, int vy) {
    }
}
