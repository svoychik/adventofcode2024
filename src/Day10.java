import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

public class Day10 {

    public static void main(String[] args) throws Exception {
        var input = Files.readString(Paths.get(Day10.class.getResource("/day10.txt").toURI()));
        System.out.println(findTrailheadScoresSum(input));
    }

    private static int findTrailheadScoresSum(String input) {
        var lines = input.split("\n");
        int rows = lines.length;
        int cols = lines[0].length();
        int[][] map = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map[i][j] = lines[i].charAt(j) - '0';
            }
        }

        int sumOfScores = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 0) {
                    sumOfScores += calculateTrailheadScore(map, r, c);
                }
            }
        }

        return sumOfScores;
    }

    private static int calculateTrailheadScore(int[][] map, int startRow, int startCol) {
        int rows = map.length;
        int cols = map[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        int score = 0;

        while (!queue.isEmpty()) {
            var current = queue.poll();
            int row = current[0], col = current[1];

            if (map[row][col] == 9) {
                score++;
                continue;
            }

            int[] dRow = {-1, 1, 0, 0};
            int[] dCol = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int newRow = row + dRow[i];
                int newCol = col + dCol[i];

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols &&
                        !visited[newRow][newCol] && map[newRow][newCol] == map[row][col] + 1) {
                    visited[newRow][newCol] = true;
                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        return score;
    }
}
