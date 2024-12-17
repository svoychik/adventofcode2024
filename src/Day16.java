import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day16 {
    // Directions: N=0, E=1, S=2, W=3
    static final int[] DX = {-1, 0, 1, 0};
    static final int[] DY = {0, 1, 0, -1};

    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day16.class.getResource("/day16.txt").toURI()));
        char[][] grid = linesToGrid(input);
        int[] start = findChar(grid, 'S');
        int[] end = findChar(grid, 'E');

        // distance array for each cell and direction
        long[][][] dist = initDist(grid);

        long answer = shortestPathCost(grid, dist, start, end);
        System.out.println(answer);

        boolean[][] onPath = findTilesOnBestPaths(grid, dist, end, answer);
        int count = countTilesOnAnyBestPath(onPath);
        System.out.println(count);
    }

    static char[][] linesToGrid(String input) {
        var lines = input.split("\\R");
        var rows = lines.length;
        var cols = lines[0].length();
        char[][] grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = lines[i].charAt(j);
            }
        }
        return grid;
    }

    static int[] findChar(char[][] grid, char c) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == c) return new int[]{i, j};
            }
        }
        throw new IllegalArgumentException("Invalid input");
    }

    // Initialize the dist array so all distances are very large initially
    static long[][][] initDist(char[][] grid) {
        long[][][] dist = new long[grid.length][grid[0].length][4];
        for (int i = 0; i < dist.length; i++)
            for (int j = 0; j < dist[i].length; j++)
                Arrays.fill(dist[i][j], Long.MAX_VALUE);
        return dist;
    }

    // Compute the shortest path cost using a Dijkstra-like algorithm.
    // Movement = 1 point, Rotation = 1000 points.
    static long shortestPathCost(char[][] grid, long[][][] dist, int[] start, int[] end) {
        dist[start[0]][start[1]][1] = 0; // Start facing East
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingLong(s -> s.cost));
        pq.add(new State(start[0], start[1], 1, 0));

        long answer = Long.MAX_VALUE;
        while (!pq.isEmpty()) {
            State cur = pq.poll();
            if (cur.cost > dist[cur.x][cur.y][cur.dir]) continue;
            if (cur.x == end[0] && cur.y == end[1]) {
                // We've reached the end with the minimal cost
                answer = cur.cost;
                break;
            }

            // Move forward
            int nx = cur.x + DX[cur.dir], ny = cur.y + DY[cur.dir];
            if (valid(grid, nx, ny)) {
                long nc = cur.cost + 1;
                if (nc < dist[nx][ny][cur.dir]) {
                    dist[nx][ny][cur.dir] = nc;
                    pq.add(new State(nx, ny, cur.dir, nc));
                }
            }

            // Rotate left (cost +1000)
            int ld = (cur.dir + 3) % 4;
            long lc = cur.cost + 1000;
            if (lc < dist[cur.x][cur.y][ld]) {
                dist[cur.x][cur.y][ld] = lc;
                pq.add(new State(cur.x, cur.y, ld, lc));
            }

            // Rotate right (cost +1000)
            int rd = (cur.dir + 1) % 4;
            long rc = cur.cost + 1000;
            if (rc < dist[cur.x][cur.y][rd]) {
                dist[cur.x][cur.y][rd] = rc;
                pq.add(new State(cur.x, cur.y, rd, rc));
            }
        }
        return answer;
    }

    static boolean valid(char[][] grid, int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && grid[x][y] != '#';
    }

    // Backtrack from the end states to find which tiles are on best paths
    static boolean[][] findTilesOnBestPaths(char[][] grid, long[][][] dist, int[] end, long answer) {
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][][] onPathState = new boolean[rows][cols][4];

        // Mark end states that achieve minimal cost
        for (int d = 0; d < 4; d++) {
            if (dist[end[0]][end[1]][d] == answer) {
                onPathState[end[0]][end[1]][d] = true;
            }
        }

        Queue<PosDir> queue = new ArrayDeque<>();
        for (int d = 0; d < 4; d++) {
            if (onPathState[end[0]][end[1]][d]) {
                queue.add(new PosDir(end[0], end[1], d));
            }
        }

        // Trace backwards to find all states that lead to end states
        while (!queue.isEmpty()) {
            var cur = queue.poll();
            long curCost = dist[cur.x][cur.y][cur.dir];

            // Backtrack from a forward step
            int px = cur.x - DX[cur.dir], py = cur.y - DY[cur.dir];
            if (valid(grid, px, py) && dist[px][py][cur.dir] == curCost - 1) {
                if (!onPathState[px][py][cur.dir]) {
                    onPathState[px][py][cur.dir] = true;
                    queue.add(new PosDir(px, py, cur.dir));
                }
            }

            // Backtrack from a left rotation
            int ld = (cur.dir + 1) % 4;
            if (dist[cur.x][cur.y][ld] == curCost - 1000) {
                if (!onPathState[cur.x][cur.y][ld]) {
                    onPathState[cur.x][cur.y][ld] = true;
                    queue.add(new PosDir(cur.x, cur.y, ld));
                }
            }

            // Backtrack from a right rotation
            int rd = (cur.dir + 3) % 4;
            if (dist[cur.x][cur.y][rd] == curCost - 1000) {
                if (!onPathState[cur.x][cur.y][rd]) {
                    onPathState[cur.x][cur.y][rd] = true;
                    queue.add(new PosDir(cur.x, cur.y, rd));
                }
            }
        }

        boolean[][] onPath = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // If any direction at this tile is on a best path, the tile is on the path
                for (int d = 0; d < 4; d++) {
                    if (onPathState[i][j][d] && grid[i][j] != '#') {
                        onPath[i][j] = true;
                        break;
                    }
                }
            }
        }

        return onPath;
    }

    static int countTilesOnAnyBestPath(boolean[][] onPath) {
        int count = 0;
        Queue<PosDir> queue = new LinkedList<>();
        for (int i = 0; i < onPath.length; i++) {
            for (int j = 0; j < onPath[i].length; j++) {
                if (onPath[i][j]) 
                    count++;
            }
        }
        return count;
    }


    record State (int x, int y, int dir, long cost) { }
    record PosDir(int x, int y, int dir) { }
}
