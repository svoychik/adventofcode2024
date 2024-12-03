import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3_2v2 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day3_2v2.class.getResource("/day3.txt").toURI()));
        boolean[] enabledState = precomputeState(input);

        // Regex for mul(x,y)
        Pattern pattern = Pattern.compile("mul\\(\\d+,\\d+\\)");
        Matcher matcher = pattern.matcher(input);
        int result = 0;
        while (matcher.find()) {
            int startIdx = matcher.start();
            if (enabledState[startIdx]) {
                result += processMul(matcher.group());
            }
        }

        System.out.println(result);
    }

    private static boolean[] precomputeState(String input) {
        boolean[] state = new boolean[input.length()];
        boolean isEnabled = true; // Start with enabled state

        for (int i = 0; i < input.length(); i++) {
            if (input.startsWith("do()", i)) {
                isEnabled = true;
            } else if (input.startsWith("don't()", i)) {
                isEnabled = false;
            }
            state[i] = isEnabled; // Record the current state
        }
        return state;
    }

    private static int processMul(String instruction) {
        return Arrays.stream(instruction
                        .replace("mul(", "")
                        .replace(")", "")
                        .split(","))
                .mapToInt(Integer::parseInt)
                .reduce((a, b) -> a * b)
                .getAsInt();
    }
}
