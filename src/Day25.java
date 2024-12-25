import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day25 {
    public static void main(String[] args) throws Exception {
        var lines = Files.readAllLines(Paths.get(Day25.class.getResource("day25.txt").toURI()));
        var data = String.join("\n", lines);

        var chunks = Arrays.stream(data.split("\\n\\n"))
                .filter(s -> !s.isBlank())
                .toList();

        var locks = new HashSet<List<Integer>>();
        var keys = new HashSet<List<Integer>>();

        for (var chunk : chunks) {
            var chunkLines = Arrays.stream(chunk.split("\\n"))
                    .filter(s -> !s.isBlank())
                    .toList();

            var heights = new int[] { -1, -1, -1, -1, -1 };
            for (var line : chunkLines) {
                for (int i = 0; i < Math.min(line.length(), 5); i++) {
                    if (line.charAt(i) == '#') {
                        heights[i]++;
                    }
                }
            }

            if (!chunkLines.isEmpty() && "#####".equals(chunkLines.get(0))) {
                locks.add(Arrays.stream(heights).boxed().collect(Collectors.toList()));
            } else {
                keys.add(Arrays.stream(heights).boxed().collect(Collectors.toList()));
            }
        }

        int compatibleCount = 0;
        for (var lock : locks) {
            for (var key : keys) {
                boolean fits = true;
                for (int i = 0; i < 5; i++) {
                    if (lock.get(i) + key.get(i) > 5) {
                        fits = false;
                        break;
                    }
                }
                if (fits) {
                    compatibleCount++;
                }
            }
        }
        System.out.println(compatibleCount);
    }
}
