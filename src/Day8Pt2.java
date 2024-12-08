import java.util.*;

public class Day8Pt2 {
    
    private static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static void main(String[] args) throws Exception {
        List<String> lines = Day8.readInput();
        char[][] map = Day8.buildMap(lines);
        Map<Character, List<Day8.Point>> antennasByFreq = Day8.parseAntennas(map);

        int rows = map.length;
        int cols = map[0].length;

        Set<Day8.Point> antinodes = new HashSet<>();

        // For each pair of antennas with the same frequency, find all points on the line
        for (var entry : antennasByFreq.entrySet()) {
            List<Day8.Point> ants = entry.getValue();
            int n = ants.size();
            if (n < 2) continue;

            for (int i = 0; i < n; i++) {
                Day8.Point p1 = ants.get(i);
                int r1 = p1.i();
                int c1 = p1.j();
                for (int j = i + 1; j < n; j++) {
                    Day8.Point p2 = ants.get(j);
                    int r2 = p2.i();
                    int c2 = p2.j();

                    int dy = r2 - r1;
                    int dx = c2 - c1;
                    int g = gcd(dy, dx);
                    dy /= g;
                    dx /= g;

                    // Move forward direction
                    int rr = r1;
                    int cc = c1;
                    while (rr >= 0 && rr < rows && cc >= 0 && cc < cols) {
                        antinodes.add(new Day8.Point(rr, cc));
                        rr += dy;
                        cc += dx;
                    }

                    // Move backward direction
                    rr = r1 - dy;
                    cc = c1 - dx;
                    while (rr >= 0 && rr < rows && cc >= 0 && cc < cols) {
                        antinodes.add(new Day8.Point(rr, cc));
                        rr -= dy;
                        cc -= dx;
                    }
                }
            }
        }

        System.out.println(antinodes.size());
    }
}
