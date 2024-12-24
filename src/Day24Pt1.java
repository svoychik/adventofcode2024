import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class Day24Pt1 {
    public static void main(String[] args) throws Exception {
        var input = Files.readAllLines(Paths.get(Day24.class.getResource("day24.txt").toURI()));
        Map<String, Integer> wires = new HashMap<>();
        List<String[]> gates = new ArrayList<>();

        boolean parsingGates = false;
        for (String line : input) {
            line = line.trim();
            if (line.isEmpty()) {
                parsingGates = true;
                continue;
            }
            if (!parsingGates) {
                String[] parts = line.split(":\\s+");
                wires.put(parts[0], Integer.parseInt(parts[1]));
            } else {
                gates.add(parseGate(line));
            }
        }

        // Simulate the gates
        Map<String, Integer> resolvedWires = simulateGates(wires, gates);

        // Extract and calculate the result
        StringBuilder binaryResult = new StringBuilder();
        resolvedWires.entrySet().stream()
                .filter(e -> e.getKey().startsWith("z"))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(e -> binaryResult.append(e.getValue()));

        var result = Long.parseLong(binaryResult.toString(), 2);
        out.println("Decimal output: " + result);
    }

    private static String[] parseGate(String line) {
        return line.split(" -> ");
    }

    private static Map<String, Integer> simulateGates(Map<String, Integer> wires, List<String[]> gates) {
        Set<String> unresolvedGates = new HashSet<>();
        for (String[] gate : gates) {
            unresolvedGates.add(gate[1]);
        }

        while (!unresolvedGates.isEmpty()) {
            Iterator<String[]> iterator = gates.iterator();
            while (iterator.hasNext()) {
                String[] gate = iterator.next();
                String expression = gate[0];
                String outputWire = gate[1];

                Matcher matcher = Pattern.compile("(.+)\\s+(AND|OR|XOR)\\s+(.+)").matcher(expression);
                if (matcher.matches()) {
                    String left = matcher.group(1);
                    String operation = matcher.group(2);
                    String right = matcher.group(3);

                    if (wires.containsKey(left) && wires.containsKey(right)) {
                        int leftValue = wires.get(left);
                        int rightValue = wires.get(right);
                        int result = switch (operation) {
                            case "AND" -> leftValue & rightValue;
                            case "OR" -> leftValue | rightValue;
                            case "XOR" -> leftValue ^ rightValue;
                            default -> throw new IllegalStateException("Unexpected operation: " + operation);
                        };
                        wires.put(outputWire, result);
                        unresolvedGates.remove(outputWire);
                        iterator.remove();
                    }
                }
            }
        }

        return wires;
    }
}
