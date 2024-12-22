import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {
    private static final long MODULO = 16_777_216L;
    private static final int STEP_COUNT = 2000;

    public static void main(String[] args) throws Exception {
        var input = Files.readAllLines(Paths.get(Day22.class.getResource("day22.txt").toURI()));

        List<Long> initialSecrets = input.stream()
                .map(Long::parseLong)
                .toList();

        long result = initialSecrets.stream()
                .mapToLong(Day22::calculate2000thSecret)
                .sum();
        System.out.println("Part 1: " + result);

        long maxBananasBFS = findMaxBananasBFS(initialSecrets);
        System.out.println("Part 2 (BFS): " + maxBananasBFS);
    }

    private static long calculate2000thSecret(long initialSecret) {
        long secret = initialSecret;
        for (int i = 0; i < 2000; i++) {
            secret = nextNumber(secret);
        }
        return secret;
    }

    private static long nextNumber(long seed) {
        long secretNumber = seed;

        // Multiply by 64
        secretNumber = ((secretNumber * 64L) ^ secretNumber) % MODULO;

        // Divide by 32, round down, mix, prune
        secretNumber = ((secretNumber / 32L) ^ secretNumber) % MODULO;

        // Multiply by 2048, mix, prune
        secretNumber = ((secretNumber * 2048L) ^ secretNumber) % MODULO;

        return secretNumber;
    }

    private static long findMaxBananasBFS(List<Long> initialSecrets) {
        Map<List<Integer>, Long> sequenceToBananas = new HashMap<>();

        for (long secret : initialSecrets) {
            Map<List<Integer>, Long> buyerSequences = countFirstOccurrence(secret);
            for (var entry : buyerSequences.entrySet()) {
                List<Integer> key = entry.getKey();
                long value = entry.getValue();
                sequenceToBananas.put(key, sequenceToBananas.getOrDefault(key, 0L) + value);
            }
        }

        return sequenceToBananas.values().stream().mapToLong(Long::longValue).max().orElse(0);
    }

    private static Map<List<Integer>, Long> countFirstOccurrence(long initialSecret) {
        Map<List<Integer>, Long> firstOccurrenceCounts = new HashMap<>();
        long secret = initialSecret;
        Queue<Integer> changes = new LinkedList<>();
        long prevPrice = secret % 10;

        for (int i = 0; i < STEP_COUNT; i++) {
            secret = nextNumber(secret);
            long currentPrice = secret % 10;
            int change = (int) (currentPrice - prevPrice);

            changes.add(change);
            if (changes.size() > 4) {
                changes.poll();
            }

            if (changes.size() == 4) {
                List<Integer> sequence = new ArrayList<>(changes);
                if (!firstOccurrenceCounts.containsKey(sequence)) {
                    firstOccurrenceCounts.put(sequence, currentPrice);
                }
            }

            prevPrice = currentPrice;
        }

        return firstOccurrenceCounts;
    }
}
