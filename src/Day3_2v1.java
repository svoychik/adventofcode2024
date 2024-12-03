import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3_2v2 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day3.class.getResource("/day3.txt").toURI()));

        Pattern pattern = Pattern.compile("mul\\(\\d+,\\d+\\)");
        Matcher matcher = pattern.matcher(input);

        boolean isEnabled = true;
        int result = 0;

        while (matcher.find()) {
            String match = matcher.group();
            var startIdx = matcher.start();
            var dontIdx = input.substring(0, startIdx).lastIndexOf("don't()");
            var doIdx = input.substring(0, startIdx).lastIndexOf("do()");
            isEnabled = dontIdx <= doIdx;
            if (isEnabled && match.startsWith("mul(")) {
                result += processMul(match);
            }
        }

        System.out.println(result);
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
