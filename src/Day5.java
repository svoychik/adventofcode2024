import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day5 {
    public static void main(String[] args) throws URISyntaxException, IOException {
        var input = Files.readString(Paths.get(Day4.class.getResource("/day5.txt").toURI()));
        String[] sections = input.split("\n\n");
        Map<Integer, List<Integer>> orderingRules = parseRules((sections[0].split("\n")));

        var updates = parseUpdates(sections[1].split("\n"));

        int totalMiddlePageSumPart1 = 0;
        int totalMiddlePageSumPart2;

        List<List<Integer>> incorrectlyOrderedUpdates = new ArrayList<>();

        for (List<Integer> update : updates) {
            if (isValidUpdate(update, orderingRules)) {
                totalMiddlePageSumPart1 += getMiddlePage(update);
            } else {
                incorrectlyOrderedUpdates.add(update);
            }
        }

        totalMiddlePageSumPart2 = incorrectlyOrderedUpdates.stream()
                .map(update -> sortUpdate(update, orderingRules))
                .mapToInt(Day5::getMiddlePage).sum();

        System.out.println("Sum of middle pages (Part 1): " + totalMiddlePageSumPart1);
        System.out.println("Sum of middle pages (Part 2): " + totalMiddlePageSumPart2);
    }

    static int getMiddlePage(List<Integer> update) {
        int middleIndex = update.size() / 2;
        return update.get(middleIndex);
    }

    static boolean isValidUpdate(List<Integer> update, Map<Integer, List<Integer>> orderMap) {
        // O(N)
        // D=O(N/2 = N)
        for (int i = 0; i < update.size(); i++) { // O(U)
            int currentPage = update.get(i);
            if (orderMap.containsKey(currentPage)) {     //O((D+U)*U)
                for (int dependentPage : orderMap.get(currentPage)) {
                    int dependentIndex = update.indexOf(dependentPage); 
                    if (dependentIndex != -1 && dependentIndex < i) {
                        return false; // Rule is violated
                    }
                }
            }
        }
        return true;
    }

    static boolean isValidUpdateV2(List<Integer> update, Map<Integer, List<Integer>> orderMap) {
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < update.size(); i++) {
            position.put(update.get(i), i);
        }

        for (var entry : orderMap.entrySet()) {
            int x = entry.getKey();
            for (int y : entry.getValue()) {
                if (position.containsKey(x) && position.containsKey(y)) {
                    if (position.get(x) >= position.get(y)) {
                        return false; // Rule x|y is violated
                    }
                }
            }
        }
        return true;
    }

    static Map<Integer, List<Integer>> parseRules(String[] rulesInput) {
        Map<Integer, List<Integer>> rules = new HashMap<>();
        for (var rule : rulesInput) {
            List<Integer> parts = Arrays.stream(rule.split("\\|")).map(Integer::parseInt).toList();
            var a = parts.get(0);
            Integer b = parts.get(1);
            if (rules.containsKey(a)) {
                rules.get(a).add(b);
            } else {
                rules.put(a, new ArrayList<>(List.of(b)));
            }
        }
        return rules;
    }

    public static List<List<Integer>> parseUpdates(String[] updatesInput) {
        List<List<Integer>> result = new ArrayList<>();
        for (String update : updatesInput) {
            List<Integer> list = Arrays.stream(update.split(","))
                    .map(Integer::parseInt)
                    .toList();
            result.add(list);
        }
        return result;
    }

    public static List<Integer> sortUpdate(List<Integer> update, Map<Integer, List<Integer>> orderingRules) {
        Map<Integer, List<Integer>> subGraph = new HashMap<>();
        for (int page : update) {
            if (orderingRules.containsKey(page)) {
                for (int neighbor : orderingRules.get(page)) {
                    if (update.contains(neighbor)) {
                        subGraph.computeIfAbsent(page, k -> new ArrayList<>()).add(neighbor);
                    }
                }
            }
        }

        return topologicalSort(subGraph, update);
    }

    public static List<Integer> topologicalSort(Map<Integer, List<Integer>> graph, List<Integer> originalOrder) {
        Map<Integer, Integer> inDegree = new HashMap<>();
        for (int node : graph.keySet()) {
            inDegree.put(node, 0);
        }
        for (List<Integer> neighbors : graph.values()) {
            for (int neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        Queue<Integer> queue = new PriorityQueue<>(Comparator.comparingInt(originalOrder::indexOf));
        for (int node : originalOrder) {
            if (inDegree.getOrDefault(node, 0) == 0) {
                queue.add(node);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            if (graph.containsKey(node)) {
                for (int neighbor : graph.get(node)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.add(neighbor);
                    }
                }
            }
        }

        return result;
    }
}
