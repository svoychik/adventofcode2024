import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day8 {
    record Point(int i, int j) {}

    public static List<String> readInput() throws Exception {
        var input = Files.readAllLines(Paths.get(Day8.class.getResource("/day8.txt").toURI()));
        return input.stream()
                .filter(line -> !line.trim().isEmpty())
                .toList();
    }

    public static char[][] buildMap(List<String> lines) {
        int rows = lines.size();
        int cols = lines.get(0).length();
        char[][] map = new char[rows][cols];

        for (int r = 0; r < rows; r++) {
            char[] arr = lines.get(r).toCharArray();
            for (int c = 0; c < cols; c++) {
                map[r][c] = arr[c];
            }
        }
        return map;
    }

    public static Map<Character, List<Point>> parseAntennas(char[][] map) {
        Map<Character, List<Point>> antennasByFreq = new HashMap<>();
        int rows = map.length;
        int cols = map[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = map[r][c];
                if (ch != '.') {
                    antennasByFreq.computeIfAbsent(ch, k -> new ArrayList<>()).add(new Point(r, c));
                }
            }
        }
        return antennasByFreq;
    }

    public static void main(String[] args) throws Exception {
        List<String> lines = readInput();
        char[][] map = buildMap(lines);
        Map<Character, List<Point>> antennasByFreq = parseAntennas(map);

        int rows = map.length;
        int cols = map[0].length;

        Set<String> antinodes = new HashSet<>();

        for (var entry : antennasByFreq.entrySet()) {
            List<Point> ants = entry.getValue();
            int n = ants.size();
            for (int i = 0; i < n; i++) {
                Point p1 = ants.get(i);
                int r1 = p1.i();
                int c1 = p1.j();
                for (int j = i + 1; j < n; j++) {
                    Point p2 = ants.get(j);
                    int r2 = p2.i();
                    int c2 = p2.j();

                    // Part 1 Antinode calculation
                    int ar1 = 2 * r1 - r2;
                    int ac1 = 2 * c1 - c2;
                    int ar2 = 2 * r2 - r1;
                    int ac2 = 2 * c2 - c1;

                    if (ar1 >= 0 && ar1 < rows && ac1 >= 0 && ac1 < cols) {
                        antinodes.add(ar1 + "," + ac1);
                    }
                    if (ar2 >= 0 && ar2 < rows && ac2 >= 0 && ac2 < cols) {
                        antinodes.add(ar2 + "," + ac2);
                    }
                }
            }
        }

        System.out.println(antinodes.size());
    }
}
