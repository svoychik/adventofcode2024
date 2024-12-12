import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Day12 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day11.class.getResource("/day12.txt").toURI()));
        System.out.println(partOne(input));
        System.out.println(partTwo(input));
    }


    static Point Up = new Point(0, -1);
    static Point Down = new Point(0, 1);
    static Point Left = new Point(-1, 0);
    static Point Right = new Point(1, 0);

    public static int partOne(String input) {
        return calculateFencePrice(input, Day12::findEdges);
    }

    public static int partTwo(String input) {
        return calculateFencePrice(input, Day12::findCorners);
    }

    private static int calculateFencePrice(String input, MeasurePerimeter measure) {
        Map<Point, Region> regions = getRegions(input);
        int totalPrice = 0;

        for (Region region : new HashSet<>(regions.values())) {
            int perimeter = 0;
            for (Point point : region.getPoints()) {
                perimeter += measure.measure(regions, point);
            }
            totalPrice += region.getPoints().size() * perimeter;
        }
        return totalPrice;
    }

    @FunctionalInterface
    interface MeasurePerimeter {
        int measure(Map<Point, Region> map, Point point);
    }

    private static int findEdges(Map<Point, Region> map, Point point) {
        int result = 0;
        Region region = map.get(point);

        for (Point direction : List.of(Right, Down, Left, Up)) {
            Point neighbor = new Point(point.x + direction.x, point.y + direction.y);
            if (!map.containsKey(neighbor) || map.get(neighbor) != region) {
                result++;
            }
        }
        return result;
    }

    private static int findCorners(Map<Point, Region> map, Point point) {
        int result = 0;
        Region region = map.get(point);

        List<Point[]> cornerDirections = List.of(
                new Point[] {Up, Right},
                new Point[] {Right, Down},
                new Point[] {Down, Left},
                new Point[] {Left, Up}
        );

        for (Point[] directions : cornerDirections) {
            Point du = directions[0];
            Point dv = directions[1];
            Point duNeighbor = new Point(point.x + du.x, point.y + du.y);
            Point dvNeighbor = new Point(point.x + dv.x, point.y + dv.y);
            Point cornerNeighbor = new Point(point.x + du.x + dv.x, point.y + du.y + dv.y);

            // Check corner types
            if ((!map.containsKey(duNeighbor) || map.get(duNeighbor) != region) &&
                    (!map.containsKey(dvNeighbor) || map.get(dvNeighbor) != region)) {
                result++;
            }

            if (map.containsKey(duNeighbor) && map.get(duNeighbor) == region &&
                    map.containsKey(dvNeighbor) && map.get(dvNeighbor) == region &&
                    (!map.containsKey(cornerNeighbor) || map.get(cornerNeighbor) != region)) {
                result++;
            }
        }
        return result;
    }

    private static Map<Point, Region> getRegions(String input) {
        String[] lines = input.split("\n");
        Map<Point, Character> garden = new HashMap<>();

        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                garden.put(new Point(x, y), lines[y].charAt(x));
            }
        }

        Map<Point, Region> result = new HashMap<>();
        Set<Point> positions = new HashSet<>(garden.keySet());

        while (!positions.isEmpty()) {
            Point pivot = positions.iterator().next();
            Region region = new Region();
            region.addPoint(pivot);

            Queue<Point> queue = new LinkedList<>();
            queue.add(pivot);
            char plantType = garden.get(pivot);

            while (!queue.isEmpty()) {
                Point current = queue.poll();
                result.put(current, region);
                positions.remove(current);

                for (Point direction : List.of(Up, Down, Left, Right)) {
                    Point neighbor = new Point(current.x + direction.x, current.y + direction.y);
                    if (!region.contains(neighbor) && positions.contains(neighbor) &&
                            garden.get(neighbor) == plantType) {
                        region.addPoint(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return result;
    }

    static class Region {
        private final Set<Point> points = new HashSet<>();

        public void addPoint(Point point) {
            points.add(point);
        }

        public boolean contains(Point point) {
            return points.contains(point);
        }

        public Set<Point> getPoints() {
            return points;
        }
    }
}
