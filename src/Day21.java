import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day21 {
    record Key(long x, long y, boolean f) {
    }

    record Point(long x, long y) {
    }

    public static void main(String[] args) throws Exception {
        var codes = Files.readAllLines(Paths.get(
                Day21.class.getResource("/day21.txt").toURI()
        ));

        var keyp = buildMap("789456123 0A");
        var dirp = buildMap(" ^A<v>");

        var result1 = execute(codes, 2, keyp, dirp);
        var result2 = execute(codes, 25, keyp, dirp);

        System.out.println(result1);
        System.out.println(result2);
    }

    static Map<Character, Point> buildMap(String s) {
        Map<Character, Point> map = new HashMap<>();
        for (var i = 0; i < s.length(); i++) {
            var c = s.charAt(i);
            map.put(c, new Point(i % 3, i / 3));
        }
        return map;
    }

    static Map<Key, Long> steps(Map<Character, Point> G, String s, long i) {
        var aPoint = G.get('A');
        var spacePoint = G.get(' ');
        if (aPoint == null || spacePoint == null) {
            return new HashMap<>();
        }
        var px = aPoint.x;
        var py = aPoint.y;
        var bx = spacePoint.x;
        var by = spacePoint.y;
        Map<Key, Long> res = new HashMap<>();
        for (var c : s.toCharArray()) {
            var nPoint = G.get(c);
            if (nPoint == null) {
                continue;
            }
            var npx = nPoint.x;
            var npy = nPoint.y;
            var f = (npx == bx && py == by) || (npy == by && px == bx);
            var key = new Key(npx - px, npy - py, f);
            res.put(key, res.getOrDefault(key, 0L) + i);
            px = npx;
            py = npy;
        }
        return res;
    }

    static long execute(List<String> codes, int n, Map<Character, Point> keyp, Map<Character, Point> dirp) {
        long r = 0;
        for (var code : codes) {
            if (code.isEmpty()) continue;
            var res = steps(keyp, code, 1L);
            for (var j = 0; j <= n; j++) {
                Map<Key, Long> res2 = new HashMap<>();
                for (var entry : res.entrySet()) {
                    var key = entry.getKey();
                    long count = entry.getValue();
                    var transformed = buildTransformedString(key.x, key.y);
                    if (key.f) {
                        transformed = new StringBuilder(transformed).reverse().toString();
                    }
                    transformed += "A";
                    var stepResult = steps(dirp, transformed, count);
                    for (var stepEntry : stepResult.entrySet()) {
                        var k = stepEntry.getKey();
                        long v = stepEntry.getValue();
                        res2.put(k, res2.getOrDefault(k, 0L) + v);
                    }
                }
                res = res2;
            }
            var total = res.values().stream().mapToLong(count -> count).sum();
            var codePrefix = code.length() >= 3 ? code.substring(0, 3) : "0";
            var codeValue = Long.parseLong(codePrefix);
            r += total * codeValue;
        }
        return r;
    }

    static String buildTransformedString(long x, long y) {
        var sb = new StringBuilder();
        if (x < 0) {
            for (var i = 0; i < -x; i++) sb.append("<");
        }
        if (y > 0) {
            for (var i = 0; i < y; i++) sb.append("v");
        }
        if (y < 0) {
            for (var i = 0; i < -y; i++) sb.append("^");
        }
        if (x > 0) {
            for (var i = 0; i < x; i++) sb.append(">");
        }
        return sb.toString();
    }
}
