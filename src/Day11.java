import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day11 {
    public static void main(String[] args) throws Exception {
        var input = Files.readString(Paths.get(Day11.class.getResource("/day11.txt").toURI()));
        List<BigInteger> stones = parseStones(input);

        int blinkCount = 75;  // Perform 75 transformation cycles
        for (int i = 0; i < blinkCount; i++) {
            System.out.println("On cycle " + i);
            stones = stones.parallelStream()
                    .flatMap(stone -> transformStone(stone).stream())
                    .toList();
        }
        
        System.out.println(stones.size());
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
    
    public static List<BigInteger> transformStone(BigInteger stone) {
        List<BigInteger> result = new ArrayList<>();
        if (stone.equals(BigInteger.ZERO)) {
            // Rule 1: Stone is 0 -> replaced by 1
            result.add(BigInteger.ONE);
        } else {
            String str = stone.toString();
            int length = str.length();
            if (length % 2 == 0) {
                // Rule 2: Even number of digits -> split into two stones
                int mid = length / 2;
                BigInteger leftNum = new BigInteger(str.substring(0, mid));
                BigInteger rightNum = new BigInteger(str.substring(mid));
                result.add(leftNum);
                result.add(rightNum);
            } else {
                // Rule 3: Odd number of digits -> multiply by 2024
                BigInteger multiplied = stone.multiply(BigInteger.valueOf(2024));
                result.add(multiplied);
            }
        }
        return result;
    }
}
