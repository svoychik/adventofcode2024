import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day24 {
    // https://www.reddit.com/r/adventofcode/comments/1hl698z/comment/m3l1rbe/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
    // https://github.com/D3STNY27/advent-of-code-2024/blob/5e690ea54c07eb3ec34998a100092f0c1dff133c/day-24/part-2.py
    // https://github.com/D3STNY27/advent-of-code-2024/tree/main/day-24 
    public static void main(String[] args) throws IOException, URISyntaxException {
        var lines = Files.readAllLines(Paths.get(Day24.class.getResource("day24.txt").toURI()));

        System.out.println("=== Part 1 Output ===");
        solvePart1(lines);

        System.out.println("\n=== Part 2 Output ===");
        solvePart2(lines);
    }

    private static void solvePart1(List<String> lines) {
        int divider = lines.indexOf("");
        List<String> initialWires = lines.subList(0, divider);
        List<String> configurations = lines.subList(divider + 1, lines.size());

        Map<String, Boolean> wiresMap = new HashMap<>();
        Set<Gate> unprocessedGates = new HashSet<>();
        Set<Gate> readyGates = new HashSet<>();
        List<WireValue> zOutputs = new ArrayList<>();

        for (String line : initialWires) {
            String[] parts = line.split(":");
            if (parts.length < 2) continue;
            wiresMap.put(parts[0].trim(), "1".equals(parts[1].trim()));
        }

        for (String gateLine : configurations) {
            if (!gateLine.contains("->")) continue;
            String[] sides = gateLine.split("->");
            String[] gateParts = sides[0].trim().split("\\s+");
            if (gateParts.length != 3) continue;
            Gate gate = new Gate(gateParts[0], gateParts[2], gateParts[1], sides[1].trim());

            if (wiresMap.containsKey(gate.wireA()) && wiresMap.containsKey(gate.wireB())) {
                readyGates.add(gate);
            } else {
                unprocessedGates.add(gate);
            }
        }

        while (true) {
            while (!readyGates.isEmpty()) {
                var gate = readyGates.iterator().next();
                readyGates.remove(gate);

                boolean aVal = wiresMap.getOrDefault(gate.wireA(), false);
                boolean bVal = wiresMap.getOrDefault(gate.wireB(), false);
                boolean outVal = switch (gate.gateType()) {
                    case "AND" -> aVal && bVal;
                    case "OR" -> aVal || bVal;
                    default -> aVal ^ bVal; // treat anything else like XOR
                };

                wiresMap.put(gate.outputWire(), outVal);
                if (gate.outputWire().startsWith("z")) {
                    zOutputs.add(new WireValue(gate.outputWire(), outVal));
                }
            }

            if (unprocessedGates.isEmpty()) break;

            Iterator<Gate> it = unprocessedGates.iterator();
            while (it.hasNext()) {
                Gate g = it.next();
                if (wiresMap.containsKey(g.wireA()) && wiresMap.containsKey(g.wireB())) {
                    readyGates.add(g);
                    it.remove();
                }
            }
        }

        zOutputs.sort(Comparator.comparing(WireValue::wireName));
        StringBuilder binaryNum = new StringBuilder();
        for (int i = zOutputs.size() - 1; i >= 0; i--) {
            binaryNum.append(zOutputs.get(i).value() ? "1" : "0");
        }
        if (!binaryNum.isEmpty()) {
            long decimalVal = Long.parseLong(binaryNum.toString(), 2);
            System.out.println(decimalVal);
        } else {
            System.out.println("No z outputs found!");
        }
    }

    private static void solvePart2(List<String> lines) {
        int divider = lines.indexOf("");
        List<String> configurations = lines.subList(divider + 1, lines.size());
        List<String> swaps = checkParallelAdders(configurations);
        Collections.sort(swaps);
        System.out.println(String.join(",", swaps));
    }

    private static String findGate(String xWire, String yWire, String gateType, List<String> configs) {
        String a = xWire + " " + gateType + " " + yWire + " -> ";
        String b = yWire + " " + gateType + " " + xWire + " -> ";
        for (String config : configs) {
            if (config.contains(a) || config.contains(b)) {
                String[] parts = config.split("->");
                if (parts.length == 2) return parts[1].trim();
            }
        }
        return null;
    }

    private static List<String> swapOutputWires(String wireA, String wireB, List<String> configs) {
        List<String> newConfigs = new ArrayList<>();
        for (String config : configs) {
            String[] parts = config.split("->");
            if (parts.length != 2) {
                newConfigs.add(config);
                continue;
            }
            String inputPart = parts[0].trim();
            String outputWire = parts[1].trim();

            if (outputWire.equals(wireA)) {
                newConfigs.add(inputPart + " -> " + wireB);
            } else if (outputWire.equals(wireB)) {
                newConfigs.add(inputPart + " -> " + wireA);
            } else {
                newConfigs.add(config);
            }
        }
        return newConfigs;
    }

    private static List<String> checkParallelAdders(List<String> originalConfigs) {
        List<String> configs = new ArrayList<>(originalConfigs);
        List<String> swaps = new ArrayList<>();
        String currentCarryWire = null;
        int bit = 0;
        // some random value, if I went to far - doesn't make sense to continue probably 
        while (bit <= 44) {
            String xWire = "x" + String.format("%02d", bit);
            String yWire = "y" + String.format("%02d", bit);
            String zWire = "z" + String.format("%02d", bit);

            if (bit == 0) {
                currentCarryWire = findGate(xWire, yWire, "AND", configs);
            } else {
                String abXorGate = findGate(xWire, yWire, "XOR", configs);
                String abAndGate = findGate(xWire, yWire, "AND", configs);
                if (abXorGate == null || abAndGate == null || currentCarryWire == null) break;

                String cinAbXorGate = findGate(abXorGate, currentCarryWire, "XOR", configs);
                if (cinAbXorGate == null) {
                    swaps.add(abXorGate);
                    swaps.add(abAndGate);
                    configs = swapOutputWires(abXorGate, abAndGate, configs);
                    bit = 0;
                    continue;
                }
                if (!cinAbXorGate.equals(zWire)) {
                    swaps.add(cinAbXorGate);
                    swaps.add(zWire);
                    configs = swapOutputWires(cinAbXorGate, zWire, configs);
                    bit = 0;
                    continue;
                }

                String cinAbAndGate = findGate(abXorGate, currentCarryWire, "AND", configs);
                if (cinAbAndGate == null) break;

                String carryWire = findGate(abAndGate, cinAbAndGate, "OR", configs);
                if (carryWire == null) break;

                currentCarryWire = carryWire;
            }

            bit++;
        }
        return swaps;
    }

    private record Gate(String wireA, String wireB, String gateType, String outputWire) {}
    private record WireValue(String wireName, boolean value) {}
}
