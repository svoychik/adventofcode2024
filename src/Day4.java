import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day4 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day4.class.getResource("/day4.txt").toURI()));
        char[][] grid = parseGrid(input);

        String word = "XMAS";
        int count = countWordOccurrences(grid, word);
        System.out.println("Total occurrences of " + word + ": " + count);
    }

    public static char[][] parseGrid(String input) {
        String[] lines = input.split("\n");
        int rows = lines.length;
        int cols = lines[0].length();
        char[][] grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            grid[i] = lines[i].toCharArray();
        }

        return grid;
    }

    public static int countWordOccurrences(char[][] grid, String word) {
        int count = 0;
        int rows = grid.length;
        int cols = grid[0].length;
        int wordLength = word.length();

        int[][] directions = {
                {0, 1},   // Right
                {1, 0},   // Down
                {1, 1},   // Diagonal Down-Right
                {1, -1},  // Diagonal Down-Left
                {0, -1},  // Left
                {-1, 0},  // Up
                {-1, -1}, // Diagonal Up-Left
                {-1, 1}   // Diagonal Up-Right
        };

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (int[] dir : directions) {
                    if (checkWord(grid, word, row, col, dir[0], dir[1], wordLength)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public static boolean checkWord(char[][] grid, String word, int startRow, int startCol, int rowDir, int colDir, int wordLength) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < wordLength; i++) {
            int newRow = startRow + i * rowDir;
            int newCol = startCol + i * colDir;

            if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
                return false;
            }

            if (grid[newRow][newCol] != word.charAt(i)) {
                return false;
            }
        }

        return true;
    }
}
