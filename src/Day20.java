import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day20 {
    private static final int[][] DIRS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public static void main(String[] args) throws Exception {
        List<String> input = Files.readAllLines(Paths.get(Day20.class.getResource("/day20.txt").toURI()));

        Map<Point, Character> graph = new HashMap<>();
        Point start = null, end = null;

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                char ch = input.get(y).charAt(x);
                Point p = new Point(x, y);
                graph.put(p, ch);
                if (ch == 'S') start = p;
                if (ch == 'E') end = p;
            }
        }

        Map<Point, Integer> dists = new HashMap<>();
        floodFill(start, dists, graph);

        int time = dists.get(end);

        int answer1 = 0, answer2 = 0;

        for (Map.Entry<Point, Integer> e1 : dists.entrySet()) {
            for (Map.Entry<Point, Integer> e2 : dists.entrySet()) {
                Point p1 = e1.getKey();
                Point p2 = e2.getKey();
                int d1 = e1.getValue();
                int d2 = e2.getValue();

                if (p1.equals(p2)) continue;

                int dist = Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);

                if (dist == 2) {
                    int shortcutTime = time - Math.abs(d1 - d2) - 1 + dist;
                    if (time - shortcutTime >= 100) answer1++;
                }

                if (dist <= 20) {
                    int shortcutTime = time - Math.abs(d1 - d2) - 1 + dist;
                    if (time - shortcutTime >= 100) answer2++;
                }
            }
        }

        answer1 = answer1 / 2;
        answer2 = answer2 / 2;

        System.out.println(answer1);
        System.out.println(answer2);
    }

    private static void floodFill(Point start, Map<Point, Integer> dists, Map<Point, Character> G) {
        Queue<PointDistance> queue = new LinkedList<>();
        queue.add(new PointDistance(start, 0));

        while (!queue.isEmpty()) {
            PointDistance pd = queue.poll();
            Point pos = pd.point;
            int d = pd.distance;

            if (dists.containsKey(pos) && d >= dists.get(pos)) continue;

            dists.put(pos, d);

            for (int[] dir : DIRS) {
                int nx = pos.x + dir[0];
                int ny = pos.y + dir[1];
                Point neighbor = new Point(nx, ny);

                if (G.getOrDefault(neighbor, '#') != '#') {
                    queue.add(new PointDistance(neighbor, d + 1));
                }
            }
        }
    }

    private record Point(int x, int y) { }
    private record PointDistance(Point point, int distance) { }
}
