import javax.swing.*;
import javax.swing.text.Keymap;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.XMLFormatter;
import java.util.stream.Collectors;

public class Day23 {
    static Set<List<String>> set = new HashSet<>();
    static HashMap<String, List<String>> graph = new HashMap<>();

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

        // Print all the values from set
        for (List<String> item : set.stream().filter(x -> x.stream().count() == 3).toList()) {
            System.out.println(item);
        }

        System.out.println("Part1 answer: " + set.stream().filter(x -> x.stream().count() == 3).count());


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
