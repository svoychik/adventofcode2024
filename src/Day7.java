import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day7 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readAllLines(Paths.get(Day7.class.getResource("/day7.txt").toURI()));
        List<Equation> equations = parseInput(input);
        long totalCalibrationResult = equations.stream()
                .filter(equation -> isEquationValid(equation.numbers, equation.target))
                .mapToLong(equation -> equation.target)
                .sum();

        System.out.println("Total Calibration Result (including concatenation): " + totalCalibrationResult);
    }

    public static List<Equation> parseInput(List<String> input) {
        List<Equation> equations = new ArrayList<>();
        for (String line : input) {
            String[] parts = line.split(":");
            long target = Long.parseLong(parts[0].trim());
            List<Long> numbers = new ArrayList<>();
            for (String num : parts[1].trim().split("\\s+")) {
                numbers.add(Long.parseLong(num));
            }
            equations.add(new Equation(target, numbers));
        }
        return equations;
    }

    public static boolean isEquationValid(List<Long> numbers, Long target) {
        return evaluate(numbers, 1, numbers.get(0), target);
    }

    public static boolean evaluate(List<Long> numbers, int index, Long currentValue, Long target) {
        if (index == numbers.size()) {
            return currentValue.equals(target);
        }

        // Try addition
        if (evaluate(numbers, index + 1, currentValue + numbers.get(index), target)) {
            return true;
        }

        // Try multiplication
        if (evaluate(numbers, index + 1, currentValue * numbers.get(index), target)) {
            return true;
        }

        // Try concatenation
        Long concatenatedValue = Long.parseLong(currentValue.toString() + numbers.get(index).toString());
        if (evaluate(numbers, index + 1, concatenatedValue, target)) {
            return true;
        }

        return false;
    }

    public static class Equation {
        Long target;
        List<Long> numbers;

        public Equation(Long target, List<Long> numbers) {
            this.target = target;
            this.numbers = numbers;
        }
    }
}
