import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day17 {
    public static void main(String[] args) throws Exception {
        List<String> input = Files.readAllLines(Paths.get(Day17.class.getResource("/day17.txt").toURI()));

        long regA = Long.parseLong(input.get(0).substring(12));
        long regB = Long.parseLong(input.get(1).substring(12));
        long regC = Long.parseLong(input.get(2).substring(12));

        String[] progParts = input.get(4).substring(9).split(",");
        long[] program = Arrays.stream(progParts).mapToLong(Long::parseLong).toArray();

        System.out.println("Part 1: " + join(runProgram(program, regA, regB, regC)));

        List<Long> target = new ArrayList<>();
        for (long p : program) {
            target.add(p);
        }

        Queue<State> variants = new ArrayDeque<>();
        variants.add(new State(0L, program.length - 1));

        while (!variants.isEmpty()) {
            State state = variants.poll();
            long partialA = state.A;
            int offset = state.offset;

            for (int i = 0; i < 8; i++) {
                long nextA = (partialA << 3) + i;
                List<Long> out = runProgram(program, nextA, 0, 0);
                List<Long> suffix = target.subList(offset, target.size());
                if (suffix.size() == out.size() && suffix.equals(out)) {
                    if (offset == 0) {
                        System.out.println("Part 2: " + nextA);
                        return;
                    } else {
                        variants.add(new State(nextA, offset - 1));
                    }
                }
            }
        }

        System.out.println("No solution found.");
    }

    record State(Long A, int offset) { }

    private static List<Long> runProgram(long[] program, long A, long B, long C) {
        int ip = 0;
        List<Long> output = new ArrayList<>();

        while (ip < program.length - 1) {
            long instr = program[ip];
            long opcode = program[ip+1];
            int nextIp = ip + 2;

            long combo = (opcode == 4 ? A :
                    opcode == 5 ? B :
                            opcode == 6 ? C : opcode);

            switch ((int)instr) {
                case 0:
                    A = A / (long)Math.pow(2, combo);
                    break;
                case 1:
                    B = B ^ opcode;
                    break;
                case 2:
                    B = combo % 8;
                    break;
                case 3:
                    if (A != 0) nextIp = (int)opcode;
                    break;
                case 4:
                    B = B ^ C;
                    break;
                case 5:
                    output.add(combo % 8);
                    break;
                case 6:
                    B = A / (long)Math.pow(2, combo);
                    break;
                case 7:
                    C = A / (long)Math.pow(2, combo);
                    break;
                default:
                    return output;
            }

            ip = nextIp;
            if (ip < 0 || ip >= program.length) break;
        }

        return output;
    }

    private static String join(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<list.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
