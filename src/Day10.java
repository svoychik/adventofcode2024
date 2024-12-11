import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

public class Day10 {

    public static void main(String[] args) throws Exception {
        var input = Files.readString(Paths.get(Day10.class.getResource("/day10.txt").toURI()));
        calculateScoresAndRatings(input);
    }

    private static void calculateScoresAndRatings(String input) {
        var lines = input.split("\n");
        var rows = lines.length;
        var cols = lines[0].length();
        var map = new int[rows][cols];

        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                map[i][j] = lines[i].charAt(j) - '0';
            }
        }

        var sumOfScores = 0;
        var sumOfRatings = 0;

        for (var r = 0; r < rows; r++) {
            for (var c = 0; c < cols; c++) {
                if (map[r][c] == 0) {
                    sumOfScores += calculateTrailheadScore(map, r, c);
                    sumOfRatings += calculateTrailheadRating(map, r, c);
                }
            }
        }

        System.out.println("Sum of trailhead scores (Part 1): " + sumOfScores);
        System.out.println("Sum of trailhead ratings (Part 2): " + sumOfRatings);
    }

    private static int calculateTrailheadScore(int[][] map, int startRow, int startCol) {
        var rows = map.length;
        var cols = map[0].length;
        var visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        var score = 0;

        while (!queue.isEmpty()) {
            var current = queue.poll();
            int row = current[0], col = current[1];

            if (map[row][col] == 9) {
                score++;
                continue;
            }

            var dRow = new int[]{-1, 1, 0, 0};
            var dCol = new int[]{0, 0, -1, 1};

            for (var i = 0; i < 4; i++) {
                var newRow = row + dRow[i];
                var newCol = col + dCol[i];

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols &&
                        !visited[newRow][newCol] && map[newRow][newCol] == map[row][col] + 1) {
                    visited[newRow][newCol] = true;
                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        return score;
    }

    private static int calculateTrailheadRating(int[][] map, int startRow, int startCol) {
        var rows = map.length;
        var cols = map[0].length;
        var distinctPaths = new int[rows][cols];
        distinctPaths[startRow][startCol] = 1;

        for (var height = 0; height < 9; height++) {
            for (var r = 0; r < rows; r++) {
                for (var c = 0; c < cols; c++) {
                    if (map[r][c] == height && distinctPaths[r][c] > 0) {
                        var dRow = new int[] { -1, 1, 0, 0 };
                        var dCol = new int[] { 0, 0, -1, 1 };

                        for (var i = 0; i < 4; i++) {
                            var newRow = r + dRow[i];
                            var newCol = c + dCol[i];

                            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols &&
                                    map[newRow][newCol] == height + 1) {
                                distinctPaths[newRow][newCol] += distinctPaths[r][c];
                            }
                        }
                    }
                }
            }
        }

        var rating = 0;
        for (var r = 0; r < rows; r++) {
            for (var c = 0; c < cols; c++) {
                if (map[r][c] == 9) {
                    rating += distinctPaths[r][c];
                }
            }
        }

        return rating;
    }
}
