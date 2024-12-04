import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day4_pt2 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day4.class.getResource("/day4.txt").toURI()));
        char[][] grid = Day4.parseGrid(input);

        int count = countXMasPatterns(grid);
        System.out.println("Total X-MAS patterns: " + count);
    }

    public static int countXMasPatterns(char[][] grid) {
        int count = 0;

        for (int row = 1; row < grid.length - 1; row++) {
            for (int col = 1; col < grid[0].length - 1; col++) {
                if (isXMasPattern(grid, row, col)) {
                    count++;
                }
            }
        }

        return count;
    }

    public static boolean isXMasPattern(char[][] grid, int centerRow, int centerCol) {
        if (grid[centerRow][centerCol] != 'A') {
            return false;
        }

        // Check the two diagonals
        String s1 = "" + grid[centerRow - 1][centerCol - 1] + grid[centerRow + 1][centerCol + 1];
        String s2 = "" + grid[centerRow + 1][centerCol - 1] + grid[centerRow - 1][centerCol + 1];

        return (s1.equals("MS") || s1.equals("SM")) && (s2.equals("MS") || s2.equals("SM"));
    }
}
