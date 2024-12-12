import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day12 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day12.class.getResource("/day12.txt").toURI()));
        String[] lines = input.split("\n");

        List<String> processed = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                processed.add(line);
            }
        }

        lines = processed.toArray(new String[0]);

        int rows = lines.length;
        int cols = lines[0].length();

        char[][] grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            String line = lines[r];
            for (int c = 0; c < cols; c++) {
                grid[r][c] = line.charAt(c);
            }
        }

        boolean[][] visited = new boolean[rows][cols];
        long totalPrice = 0;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!visited[r][c]) {
                    char plantType = grid[r][c];

                    // Find all cells in this region using BFS
                    List<int[]> cells = new ArrayList<>();
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{r, c});
                    visited[r][c] = true;

                    while (!queue.isEmpty()) {
                        int[] current = queue.poll();
                        int cr = current[0], cc = current[1];
                        cells.add(new int[]{cr, cc});

                        for (int i = 0; i < 4; i++) {
                            int nr = cr + dr[i];
                            int nc = cc + dc[i];
                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                                if (!visited[nr][nc] && grid[nr][nc] == plantType) {
                                    visited[nr][nc] = true;
                                    queue.offer(new int[]{nr, nc});
                                }
                            }
                        }
                    }

                    // Calculate area
                    int area = cells.size();

                    // Calculate perimeter
                    int perimeter = 0;
                    for (int[] cell : cells) {
                        int rr = cell[0], cc = cell[1];
                        for (int i = 0; i < 4; i++) {
                            int nr = rr + dr[i];
                            int nc = cc + dc[i];
                            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || grid[nr][nc] != plantType) {
                                perimeter++;
                            }
                        }
                    }

                    long price = (long) area * perimeter;
                    totalPrice += price;
                }
            }
        }

        System.out.println(totalPrice);
    }
}
