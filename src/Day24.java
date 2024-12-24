import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day24 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        // Read all lines from the resource "day24.txt" (make sure the file is in the correct resource folder)
        var lines = Files.readAllLines(Paths.get(Day24.class.getResource("day24.txt").toURI()));

        System.out.println("=== Part 1 Output ===");
        solvePart1(lines);

        System.out.println("\n=== Part 2 Output ===");
        solvePart2(lines);
    }

    // ------------------------
    // Part 1 (Python snippet #1)
    // ------------------------
    private static void solvePart1(List<String> lines) {
        // The original Python code splits the input at a blank line
        // so first, find the index of the blank line
        int divider = lines.indexOf("");

        // initial_wires = lines[:divider]
        List<String> initialWires = lines.subList(0, divider);

        // configurations = lines[divider+1:]
        List<String> configurations = lines.subList(divider + 1, lines.size());

        // Wires map: wireName -> booleanValue
        Map<String, Boolean> wiresMap = new HashMap<>();

        // Sets for gates
        Set<Gate> unprocessedGates = new HashSet<>();
        Set<Gate> readyGates = new HashSet<>();

        // We'll store z outputs in a list so we can eventually build the binary number
        List<WireValue> zOutputs = new ArrayList<>();

        // 1) Parse initial wires
        for (String line : initialWires) {
            // e.g. "wireName: 1"
            String[] parts = line.split(":");
            if (parts.length < 2) {
                continue;
            }
            String wireName = parts[0].trim();
            String wireValue = parts[1].trim();

            // Store in wiresMap (1 -> true, 0 -> false)
            wiresMap.put(wireName, "1".equals(wireValue));
        }

        // 2) Parse gates in configurations
        for (String gateLine : configurations) {
            // e.g. "x0 AND y0 -> z0"
            if (!gateLine.contains("->")) {
                continue;
            }
            String[] sides = gateLine.split("->");
            String inputConfig = sides[0].trim();  // "x0 AND y0"
            String outputWire = sides[1].trim();   // "z0"

            // Then parse wireA, gateType, wireB from "x0 AND y0"
            String[] gateParts = inputConfig.split("\\s+");
            if (gateParts.length != 3) {
                continue;
            }
            String wireA = gateParts[0];
            String gateType = gateParts[1];
            String wireB = gateParts[2];

            Gate gate = new Gate(wireA, wireB, gateType, outputWire);

            // If wireA and wireB are already known in wiresMap, it's ready
            if (wiresMap.containsKey(wireA) && wiresMap.containsKey(wireB)) {
                readyGates.add(gate);
            } else {
                unprocessedGates.add(gate);
            }
        }

        // 3) Process gates
        while (true) {
            // While there's at least one gate we can process
            while (!readyGates.isEmpty()) {
                Gate gate = readyGates.iterator().next();
                readyGates.remove(gate);

                boolean wireAVal = wiresMap.getOrDefault(gate.wireA(), false);
                boolean wireBVal = wiresMap.getOrDefault(gate.wireB(), false);
                boolean outputVal;

                switch (gate.gateType()) {
                    case "AND":
                        outputVal = wireAVal && wireBVal;
                        break;
                    case "OR":
                        outputVal = wireAVal || wireBVal;
                        break;
                    // The Python code uses XOR as a fallback ( != ), so let's do the same
                    default: // "XOR"
                        outputVal = (wireAVal ^ wireBVal);
                        break;
                }

                wiresMap.put(gate.outputWire(), outputVal);

                // If the outputWire starts with "z", collect it for later
                if (gate.outputWire().startsWith("z")) {
                    zOutputs.add(new WireValue(gate.outputWire(), outputVal));
                }
            }

            if (unprocessedGates.isEmpty()) {
                // No more gates to process
                break;
            }

            // Otherwise, some gates might become ready now that we've updated wiresMap
            Iterator<Gate> it = unprocessedGates.iterator();
            while (it.hasNext()) {
                Gate g = it.next();
                if (wiresMap.containsKey(g.wireA()) && wiresMap.containsKey(g.wireB())) {
                    readyGates.add(g);
                    it.remove(); // remove from unprocessed
                }
            }
        }

        // 4) Build binary number from sorted z outputs
        // sorted by wire name, then build the number from least significant to most (the Python code appended in reverse)
        zOutputs.sort(Comparator.comparing(WireValue::wireName));
        // The Python snippet built the binary from right to left, so we’ll replicate that
        // but we can just build from left->right if we reverse the iteration
        StringBuilder binaryNum = new StringBuilder();
        for (int i = zOutputs.size() - 1; i >= 0; i--) {
            boolean val = zOutputs.get(i).value();
            binaryNum.append(val ? "1" : "0");
        }

        // Convert binary to decimal
        // If binaryNum is empty, just skip
        if (!binaryNum.isEmpty()) {
            var decimalVal = Long.parseLong(binaryNum.toString(), 2);
            System.out.println(decimalVal);
        } else {
            System.out.println("No z outputs found or something went wrong!");
        }
    }

    // ------------------------
    // Part 2 (Python snippet #2)
    // ------------------------
    private static void solvePart2(List<String> lines) {
        int divider = lines.indexOf("");
        // Configurations = lines after the blank line
        List<String> configurations = lines.subList(divider + 1, lines.size());

        // 1) Identify which wires need swapping to ensure a parallel adder layout
        List<String> swaps = checkParallelAdders(configurations);

        // 2) Print the sorted swaps
        Collections.sort(swaps);
        System.out.println(String.join(",", swaps));
    }

    /**
     * Looks for a particular gate in the configurations. E.g. x_wire <gate_type> y_wire -> something
     * or y_wire <gate_type> x_wire -> something
     */
    private static String findGate(String xWire, String yWire, String gateType, List<String> configurations) {
        String subStrA = xWire + " " + gateType + " " + yWire + " -> ";
        String subStrB = yWire + " " + gateType + " " + xWire + " -> ";

        for (String config : configurations) {
            if (config.contains(subStrA) || config.contains(subStrB)) {
                String[] parts = config.split("->");
                if (parts.length == 2) {
                    return parts[1].trim();
                }
            }
        }
        return null;
    }

    /**
     * Swap output wires in the configuration lines. If output wire = wireA, replace with wireB, etc.
     */
    private static List<String> swapOutputWires(String wireA, String wireB, List<String> configurations) {
        List<String> newConfigs = new ArrayList<>();

        for (String config : configurations) {
            String[] parts = config.split("->");
            if (parts.length != 2) {
                // Not a valid gate line, just keep as is
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

    /**
     * The core logic to check parallel adders from the second snippet.
     */
    private static List<String> checkParallelAdders(List<String> originalConfigs) {
        // We'll modify configurations as we go along
        List<String> configurations = new ArrayList<>(originalConfigs);
        List<String> swaps = new ArrayList<>();

        String currentCarryWire = null;
        int bit = 0;

        while (true) {
            String xWire = String.format("x%02d", bit);
            String yWire = String.format("y%02d", bit);
            String zWire = String.format("z%02d", bit);

            // For bit == 0, we look for x0 AND y0 -> carry
            if (bit == 0) {
                currentCarryWire = findGate(xWire, yWire, "AND", configurations);
            } else {
                // Find xWire XOR yWire -> ?
                String abXorGate = findGate(xWire, yWire, "XOR", configurations);
                // Find xWire AND yWire -> ?
                String abAndGate = findGate(xWire, yWire, "AND", configurations);

                // cin_ab_xor_gate = find_gate(ab_xor_gate, current_carry_wire, 'XOR')
                if (abXorGate == null || abAndGate == null || currentCarryWire == null) {
                    // If we don't find them, there's nothing else to do
                    break;
                }

                String cinAbXorGate = findGate(abXorGate, currentCarryWire, "XOR", configurations);
                if (cinAbXorGate == null) {
                    // If this gate doesn't exist, we do a swap and restart
                    swaps.add(abXorGate);
                    swaps.add(abAndGate);
                    configurations = swapOutputWires(abXorGate, abAndGate, configurations);
                    bit = 0;
                    continue;
                }

                // If the output of that XOR isn't zWire, we also swap
                if (!cinAbXorGate.equals(zWire)) {
                    swaps.add(cinAbXorGate);
                    swaps.add(zWire);
                    configurations = swapOutputWires(cinAbXorGate, zWire, configurations);
                    bit = 0;
                    continue;
                }

                // Next, we find xWire XOR yWire AND currentCarryWire -> someWire
                String cinAbAndGate = findGate(abXorGate, currentCarryWire, "AND", configurations);
                if (cinAbAndGate == null) {
                    break;
                }

                // carry_wire = find_gate(ab_and_gate, cin_ab_and_gate, 'OR')
                String carryWire = null;
                carryWire = findGate(abAndGate, cinAbAndGate, "OR", configurations);
                if (carryWire == null) {
                    break;
                }

                currentCarryWire = carryWire;
            }

            bit++;
            // Arbitrary cutoff in the Python snippet was 45
            if (bit >= 45) {
                break;
            }
        }

        return swaps;
    }

    // A helper record/class to store gate data (wireA, wireB, gateType, outputWire)
    private record Gate(String wireA, String wireB, String gateType, String outputWire) {}

    // A helper record/class to store (wireName, boolean value)
    private record WireValue(String wireName, boolean value) {}
}
