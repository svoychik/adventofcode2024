import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

// NOTE: dynamic programming
public class Day19 {
    public static void main(String[] args) throws Exception {
        List<String> input = Files.readAllLines(Paths.get(Day19.class.getResource("/day19.txt").toURI()));
        var towelPatterns = new HashSet<String>();
        var designs = new ArrayList<String>();
        var isDesignSection = false;

        for (String line : input) {
            line = line.trim();
            if (line.isEmpty()) {
                isDesignSection = true;
                continue;
            }
            if (!isDesignSection) {
                String[] patterns = line.split(",");
                for (String pattern : patterns) {
                    pattern = pattern.trim();
                    if (!pattern.isEmpty()) {
                        towelPatterns.add(pattern);
                    }
                }
            } else {
                if (!line.isEmpty()) {
                    designs.add(line);
                }
            }
        }

        if (!isDesignSection && !input.isEmpty()) {
            String[] patterns = input.get(0).split(",");
            for (String pattern : patterns) {
                pattern = pattern.trim();
                if (!pattern.isEmpty()) {
                    towelPatterns.add(pattern);
                }
            }
            for (int i = 1; i < input.size(); i++) {
                String design = input.get(i).trim();
                if (!design.isEmpty()) {
                    designs.add(design);
                }
            }
        }

        long possibleCount = 0;
        long totalWays = 0;
        for (String design : designs) {
            long ways = countConstruct(design, towelPatterns);
            if (ways > 0) {
                possibleCount++;
                totalWays += ways;
            }
        }

        System.out.println("Part 1: " + possibleCount);
        System.out.println("Part 2: " + totalWays);

    }

    private static long countConstruct(String target, Set<String> patterns) {
        var n = target.length();
        long[] dp = new long[n + 1];
        dp[0] = 1;

        for (int i = 1; i <= n; i++) {
            for (String pattern : patterns) {
                int len = pattern.length();
                if (len <= i && target.substring(i - len, i).equals(pattern)) {
                    dp[i] += dp[i - len];
                }
            }
        }

        return dp[n];
    }
}
