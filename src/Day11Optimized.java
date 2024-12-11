import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Day11Optimized {

    private static final Map<CacheKey, Long> cache = new ConcurrentHashMap<>();
    record CacheKey(BigInteger stone, int count) {}

    public static void main(String[] args) throws Exception {
        var input = Files.readString(Paths.get(Day11.class.getResource("/day11.txt").toURI()));
        List<BigInteger> initialStones = parseStones(input);
        System.out.println(calc(25, initialStones));
        System.out.println(calc(75, initialStones));
    }

    private static List<BigInteger> parseStones(String input) {
        List<BigInteger> stones = new ArrayList<>();
        if (input == null || input.isBlank()) {
            return stones;
        }

        String[] parts = input.trim().split("\\s+");
        for (String part : parts) {
            stones.add(new BigInteger(part));
        }
        return stones;
    }

    static long calc(int cnt, List<BigInteger> stones) {
        if (cnt == 0) {
            return stones.size();
        }

        return stones.stream().mapToLong(stone -> calc(cnt, stone)).sum();
    }

    static long calc(int cnt, BigInteger stone) {
        CacheKey key = new CacheKey(stone, cnt);
        Long cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        final long result;
        if (cnt == 0) {
            // No transformations needed
            result = 1;
        } else {
            // Apply one transformation and compute result for cnt-1
            List<BigInteger> nextStones = Day11.transformStone(stone);
            if (cnt == 1) {
                // After one transformation, count resulting stones
                result = nextStones.size();
            } else {
                // Recursively compute the sum of the outcomes after (cnt-1) more blinks
                long sum = 0;
                for (BigInteger s : nextStones) {
                    sum += calc(cnt - 1, s);
                }
                result = sum;
            }
        }

        cache.put(key, result);
        return result;
    }
}
