import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day23 {
    static Set<List<String>> set = new HashSet<>();
    static HashMap<String, List<String>> graph = new HashMap<>();
    static int maxCliqueSize = 0;
    
    public static void main(String[] args) throws Exception {
        var lines = Files.readAllLines(Paths.get(Day22.class.getResource("day23.txt").toURI()));
        for (String line : lines) {
            var parts = line.split("-");
            var node2 = parts[0];
            var node1 = parts[1];
            graph.computeIfAbsent(node1, k -> new ArrayList<>()).add(node2);
            graph.computeIfAbsent(node2, k -> new ArrayList<>()).add(node1);
        }

        var keysStartingWithT = graph.keySet().stream().filter(x -> x.startsWith("t")).toList();
        keysStartingWithT
                .forEach(key -> Dfs(key, key, new HashSet<>()));

        for (List<String> item : set.stream().filter(x -> x.stream().count() == 3).toList()) {
            System.out.println(item);
        }

        System.out.println("Part1 answer: " + set.stream().filter(x -> x.stream().count() == 3).count());

        // Find the largest clique
        var largestClique = findLargestClique();
        var password = String.join(",", largestClique.stream().sorted().toList());
        System.out.println("Password is; " + password);
    }

    private static Set<String> findLargestClique() {
        Set<String> largestClique = new HashSet<>();

        // starting from every node
        for (String node : graph.keySet()) {
            Set<String> currentClique = new HashSet<>();
            currentClique.add(node); // Start with the current node

            // Attempt to expand the clique
            for (String neighbor : graph.get(node)) {
                if (currentClique.stream().allMatch(member -> graph.get(member).contains(neighbor))) {
                    currentClique.add(neighbor);
                }
            }

            // Update the largest clique if the current one is bigger
            if (currentClique.size() > largestClique.size()) {
                largestClique = currentClique;
            }
        }

        return largestClique;
    }
    
    private static List<String> findClique(String node, Set<String> currentClique) {
        currentClique.add(node);

        List<String> candidates = graph.get(node).stream()
                .filter(neighbor -> currentClique.stream().allMatch(n -> graph.get(neighbor).contains(n)))
                .toList();

        // Prune if this branch cannot produce a larger clique
        if (currentClique.size() + candidates.size() <= maxCliqueSize) {
            return new ArrayList<>(currentClique);
        }

        List<String> largestSubClique = new ArrayList<>(currentClique);

        for (String candidate : candidates) {
            if (!currentClique.contains(candidate)) {
                Set<String> newClique = new HashSet<>(currentClique);
                List<String> subClique = findClique(candidate, newClique);
                if (subClique.size() > largestSubClique.size()) {
                    largestSubClique = subClique;
                }
            }
        }

        maxCliqueSize = Math.max(maxCliqueSize, largestSubClique.size());
        return largestSubClique;
    }

    private static void Dfs(String start, String key, Set<String> nodes) {
        if (nodes.size() > 3)
            return;
        var children = graph.get(key);
        nodes.add(key);
        if (nodes.size() == 3 && children.contains(start)) {
            set.add(nodes.stream().sorted().collect(Collectors.toList()));
            return;
        }


        for (var child : children) {
            boolean visited = nodes.contains(child);
            if (!visited) {
                Dfs(start, child, nodes);
                nodes.remove(child);
            }

        }
    }
}
