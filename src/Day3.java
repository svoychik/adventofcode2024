import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day3 {
    public static void main(String[] args) throws IOException, URISyntaxException {

        var input = Files.readString(Paths.get(Day1.class.getResource("/day3.txt").toURI()));
        //match mul(x,y)
        Pattern pattern = Pattern.compile("mul\\(\\d+,\\d+\\)");
        Matcher matcher = pattern.matcher(input);

        List<String> matches = new ArrayList<>();

        while (matcher.find()) {
            matches.add(matcher.group());
        }
        var res = 0;
        for (String match : matches) {
            res += Arrays.stream(match.replace("mul(", "")
                    .replace(")", "")
                    .split(","))
                    .mapToInt(Integer::parseInt)
                    .reduce((a, b) -> a * b)
                    .getAsInt();
        }
        System.out.println(res);
    }
}