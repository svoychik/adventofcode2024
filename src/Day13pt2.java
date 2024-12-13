import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13pt2 {
    private static final long BUTTON_A_TOKENS = 3;
    private static final long BUTTON_B_TOKENS = 1;

    public static void main(String[] args) throws Exception {
        var input = Files.readString(Paths.get(Day13pt2.class.getResource("/day13.txt").toURI()));
        var pattern = Pattern.compile(
                        "Button A: X\\+(\\d+), Y\\+(\\d+)\\s+" +
                        "Button B: X\\+(\\d+), Y\\+(\\d+)\\s+" +
                        "Prize: X=(\\d+), Y=(\\d+)"
        );
        Matcher matcher = pattern.matcher(input);

        long pt1Tokens = 0;
        long pt2Tokens = 0;

        while (matcher.find()) {
            long ax = Long.parseLong(matcher.group(1));
            long ay = Long.parseLong(matcher.group(2));
            long bx = Long.parseLong(matcher.group(3));
            long by = Long.parseLong(matcher.group(4));
            long px = Long.parseLong(matcher.group(5));
            long py = Long.parseLong(matcher.group(6));

            pt1Tokens += calculateTokens(ax, ay, bx, by, px, py, 0L);
            pt2Tokens += calculateTokens(ax, ay, bx, by, px, py, 10_000_000_000_000L);
        }

        System.out.println("pt1: " + pt1Tokens);
        System.out.println("pt2: " + pt2Tokens);
    }

    private static long calculateTokens(long ax, long ay, long bx, long by, long px, long py, long prizeDistance) {
        long determinant = ax * by - ay * bx;
        if (determinant == 0) return 0; // Avoid division by zero

        long pxShifted = px + prizeDistance;
        long pyShifted = py + prizeDistance;

        long mx = by * pxShifted - bx * pyShifted;
        long my = -ay * pxShifted + ax * pyShifted;

        if (mx % determinant == 0 && my % determinant == 0) {
            long abx = mx / determinant;
            long aby = my / determinant;
            return BUTTON_A_TOKENS * abx + BUTTON_B_TOKENS * aby;
        } else {
            return 0;
        }
    }
}
