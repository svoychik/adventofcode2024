import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day23 {
    static Set<List<String>> set = new HashSet<>();
    static HashMap<String, List<String>> graph = new HashMap<>();

    public static void main(String[] args) throws Exception {
        var lines = Files.readAllLines(Paths.get(Day23.class.getResource("day23.txt").toURI()));
        for (String line : lines) {
            var parts = line.split("-");
            var node1 = parts[0];
            var node2 = parts[1];
            graph.computeIfAbsent(node1, k -> new ArrayList<>()).add(node2);
            graph.computeIfAbsent(node2, k -> new ArrayList<>()).add(node1);
        }

        // Perform DFS starting from each node
        for (String start : graph.keySet()) {
            Dfs(start, start, new LinkedList<>());
        }

        // Count triangles containing at least one node starting with 't'
        int result = (int) set.stream().filter(triangle ->
                triangle.stream().anyMatch(node -> node.startsWith("t"))
        ).count();

        // Print all triangles containing 't'
        set.stream()
                .filter(triangle -> triangle.stream().anyMatch(node -> node.startsWith("t")))
                .forEach(System.out::println);

        System.out.println("Part1 answer: " + result);
    }

    private static void Dfs(String start, String current, LinkedList<String> path) {
        if (path.size() > 3) 
            return;

        path.add(current);
        if (path.size() == 3 && graph.get(current).contains(start)) {
            set.add(new ArrayList<>(path).stream().sorted().toList());
        }

        // Continue DFS on unvisited neighbors
        for (String neighbor : graph.get(current)) {
            boolean visited;
            if (path.contains(neighbor)) visited = true;
            else visited = false;
            if (!visited) { 
                Dfs(start, neighbor, new LinkedList<>(path));
            }
        }
    }
}
