import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Day12 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day12.class.getResource("/day12.txt").toURI()));
        System.out.println(partOne(input));
        System.out.println(partTwo(input));
    }

    private static final List<Point> DIRECTIONS = List.of(
            new Point(0, -1), // Up
            new Point(0, 1),  // Down
            new Point(-1, 0), // Left
            new Point(1, 0)   // Right
    );

    private static final List<Point[]> CORNER_DIRECTIONS = List.of(
            new Point[]{new Point(0, -1), new Point(1, 0)},  // Up-Right
            new Point[]{new Point(1, 0), new Point(0, 1)},   // Right-Down
            new Point[]{new Point(0, 1), new Point(-1, 0)},  // Down-Left
            new Point[]{new Point(-1, 0), new Point(0, -1)}  // Left-Up
    );

    public static int partOne(String input) {
        return calculateFencePrice(input, Day12::countEdges);
    }

    public static int partTwo(String input) {
        return calculateFencePrice(input, Day12::countCorners);
    }

    private static int calculateFencePrice(String input, Measure measure) {
        Map<Point, Character> garden = parseGarden(input);
        Set<Point> visited = new HashSet<>();
        int totalPrice = 0;

        for (Point point : garden.keySet()) {
            if (!visited.contains(point)) {
                char plantType = garden.get(point);
                Set<Point> region = findRegion(garden, point, plantType);
                visited.addAll(region);
                int perimeter = region.stream().mapToInt(p -> measure.apply(garden, p, plantType)).sum();
                totalPrice += region.size() * perimeter;
            }
        }

        return totalPrice;
    }

    private static int countEdges(Map<Point, Character> garden, Point point, char plantType) {
        int edges = 0;
        for (Point direction : DIRECTIONS) {
            Point neighbor = new Point(point.x + direction.x, point.y + direction.y);
            if (garden.getOrDefault(neighbor, '\0') != plantType) {
                edges++;
            }
        }
        return edges;
    }

    private static int countCorners(Map<Point, Character> garden, Point point, char plantType) {
        int corners = 0;
        for (Point[] pair : CORNER_DIRECTIONS) {
            Point neighbor1 = new Point(point.x + pair[0].x, point.y + pair[0].y);
            Point neighbor2 = new Point(point.x + pair[1].x, point.y + pair[1].y);
            Point corner = new Point(point.x + pair[0].x + pair[1].x, point.y + pair[0].y + pair[1].y);

            if ((garden.getOrDefault(neighbor1, '\0') != plantType &&
                    garden.getOrDefault(neighbor2, '\0') != plantType) ||
                    (garden.getOrDefault(neighbor1, plantType) == plantType &&
                            garden.getOrDefault(neighbor2, plantType) == plantType &&
                            garden.getOrDefault(corner, '\0') != plantType)) {
                corners++;
            }
        }
        return corners;
    }

    private static Map<Point, Character> parseGarden(String input) {
        Map<Point, Character> garden = new HashMap<>();
        String[] lines = input.split("\n");
        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                garden.put(new Point(x, y), lines[y].charAt(x));
            }
        }
        return garden;
    }

    private static Set<Point> findRegion(Map<Point, Character> garden, Point start, char plantType) {
        Set<Point> region = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        region.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            for (Point direction : DIRECTIONS) {
                Point neighbor = new Point(current.x + direction.x, current.y + direction.y);
                if (!region.contains(neighbor) && garden.getOrDefault(neighbor, '\0') == plantType) {
                    region.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return region;
    }

    @FunctionalInterface
    interface Measure {
        int apply(Map<Point, Character> garden, Point point, char plantType);
    }
}
