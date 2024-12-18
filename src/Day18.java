import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.LinkedList;

public class Day18 {
    private static final int SIZE = 71;
    private static final int[][] DIRECTIONS = {
            {0, 1},   // Down
            {0, -1},  // Up
            {1, 0},   // Right
            {-1, 0}   // Left
    };

    public static void main(String[] args) throws Exception {
        var input = Files.readAllLines(Paths.get(Day18.class.getResource("/day18.txt").toURI()));

        var corrupted = new boolean[SIZE][SIZE];

        for (var i = 0; i < 1024 && i < input.size(); i++) {
            var line = input.get(i);
            var parts = line.split(",");
            var x = Integer.parseInt(parts[0].trim());
            var y = Integer.parseInt(parts[1].trim());
            if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
                continue;
            }
            corrupted[y][x] = true;
        }

        var part1Steps = findShortestPath(corrupted, 0, 0, SIZE - 1, SIZE - 1);
        System.out.println("Part 1: Minimum steps = " + part1Steps);

        // Reset corrupted grid
        corrupted = new boolean[SIZE][SIZE];
        String blockingByte = null;
        for (var i = 0; i < input.size(); i++) {
            var line = input.get(i);
            var parts = line.split(",");
            var x = Integer.parseInt(parts[0].trim());
            var y = Integer.parseInt(parts[1].trim());
            if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
                continue;
            }

            corrupted[y][x] = true;

            // After each corruption, check if a path still exists
            if (!isPathAvailable(corrupted, 0, 0, SIZE - 1, SIZE - 1)) {
                blockingByte = x + "," + y;
                break;
            }
        }

        System.out.println("Part 2: Blocking byte = " + blockingByte);
    }


    static int findShortestPath(boolean[][] corrupted, int startX, int startY, int endX, int endY) {
        // If start or end is corrupted, no path exists
        if (corrupted[startY][startX] || corrupted[endY][endX]) {
            return -1;
        }

        var visited = new boolean[SIZE][SIZE];
        Queue<Node> q = new LinkedList<>();
        q.add(new Node(startX, startY, 0));
        visited[startY][startX] = true;

        while (!q.isEmpty()) {
            var current = q.poll();

            // reached the exit?
            if (current.x == endX && current.y == endY) {
                return current.steps;
            }

            for (var dir : DIRECTIONS) {
                var nx = current.x + dir[0];
                var ny = current.y + dir[1];
                if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE) {
                    continue;
                }
                if (!corrupted[ny][nx] && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    q.add(new Node(nx, ny, current.steps + 1));
                }
            }
        }
        return -1;
    }

    static boolean isPathAvailable(boolean[][] corrupted, int startX, int startY, int endX, int endY) {
        if (corrupted[startY][startX] || corrupted[endY][endX]) {
            return false;
        }

        var visited = new boolean[SIZE][SIZE];
        Queue<Node> q = new LinkedList<>();
        q.add(new Node(startX, startY, 0));
        visited[startY][startX] = true;

        while (!q.isEmpty()) {
            var current = q.poll();

            if (current.x == endX && current.y == endY) {
                return true;
            }
            for (var dir : DIRECTIONS) {
                var nx = current.x + dir[0];
                var ny = current.y + dir[1];
                if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE) {
                    continue;
                }

                if (!corrupted[ny][nx] && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    q.add(new Node(nx, ny, current.steps + 1));
                }
            }
        }

        return false;
    }

    record Node(int x, int y, int steps) { }
}
