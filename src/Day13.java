import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 {

    public static void main(String[] args) throws Exception {
        var input = Files.readString(Paths.get(Day13.class.getResource("/day13.txt").toURI()));

        int totalTokens = 0;
        int prizesWon = 0;
        var pattern = Pattern.compile(
                "Button A: X\\+(\\      d+), Y\\+(\\d+)\\s+" +
                        "Button B: X\\+(\\d+), Y\\+(\\d+)\\s+" +
                        "Prize: X=(\\d+), Y=(\\d+)"
        );
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            int aX = Integer.parseInt(matcher.group(1));
            int aY = Integer.parseInt(matcher.group(2));
            int bX = Integer.parseInt(matcher.group(3));
            int bY = Integer.parseInt(matcher.group(4));
            int targetX = Integer.parseInt(matcher.group(5));
            int targetY = Integer.parseInt(matcher.group(6));
            int[] result = solveDiophantine(aX, aY, bX, bY, targetX, targetY);
            if (result != null) {
                prizesWon++;
                totalTokens += result[0];
            }
        }
        System.out.println("Prizes won: " + prizesWon);
        System.out.println("Total tokens: " + totalTokens);
    }

    private static int[] solveDiophantine(int aX, int aY, int bX, int bY, int targetX, int targetY) {
        int limit = 100;
        int minTokens = Integer.MAX_VALUE;
        int optimalX = -1;
        int optimalY = -1;

        for (int x = 0; x <= limit; x++) {
            for (int y = 0; y <= limit; y++) {
                if (aX * x + bX * y == targetX && aY * x + bY * y == targetY) {
                    int cost = 3 * x + y;
                    if (cost < minTokens) {
                        minTokens = cost;
                        optimalX = x;
                        optimalY = y;
                    }
                }
            }
        }

        return minTokens == Integer.MAX_VALUE ? null : new int[]{minTokens, optimalX, optimalY};
    }
}
