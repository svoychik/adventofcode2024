import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day2 {
    public static void main(String[] args) throws IOException, URISyntaxException {

        var lines = Files.readAllLines(Paths.get(Day1.class.getResource("/day2.txt").toURI()));
        var safeReports = 0;
        var safeWithDampener = 0;
        for (var line : lines) {
            int[] arr = Arrays.stream(line.split("\\s+")).mapToInt(Integer::parseInt).toArray();
            if ((isGraduallyDecr(arr) || isGraduallyInc(arr)) && isDiffSmall(arr))
                safeReports++;
            if(isSafeWithDampener(arr))
                safeWithDampener++;
        }
        System.out.println(safeReports);
        System.out.println(safeWithDampener);                                    
    }

    private static boolean isSafe(int[] arr) {
        return (isGraduallyInc(arr) || isGraduallyDecr(arr)) && isDiffSmall(arr);
    }

    private static boolean isSafeWithDampener(int[] arr) {
        // Simulate removing each level
        // Create a new array without the ith element      
        return isSafe(arr) // already safe 
                || IntStream.range(0, arr.length).mapToObj(i -> removeElementAtIndex(arr, i)).anyMatch(Day2::isSafe);
    }

    private static boolean isSafeWithDampener2(int[] arr) {
        int violations = 0;

        for (int i = 1; i < arr.length; i++) {
            if ((arr[i] < arr[i - 1] && !isGraduallyDecr(arr)) || (arr[i] > arr[i - 1] && !isGraduallyInc(arr))
                    || Math.abs(arr[i] - arr[i - 1]) > 3) {
                violations++;
                if (violations > 1) return false;
            }
        }
        return true;
    }
    
    // another way of doing that
    public static boolean isOrdered(List<Integer> levels) {
        var isAscending = IntStream.range(0, levels.size() - 1)
                .allMatch(i -> levels.get(i) <= levels.get(i + 1));
        var isDescending = IntStream.range(0, levels.size() - 1)
                .allMatch(i -> levels.get(i) >= levels.get(i + 1));

        return isAscending || isDescending;
    }
    
    private static boolean isDiffSmall(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            var diff1 = Math.abs(arr[i] - arr[i - 1]);
            if (diff1 < 1 || diff1 > 3)
                return false;
        }
        return true;
    }

    private static boolean isGraduallyInc(int[] arr) {
        for(var i = 1; i < arr.length; i++) {
            if(arr[i - 1] < arr[i])
                continue;
            else return false;
        }
        return true;
    }

    private static boolean isGraduallyDecr(int[] arr) {
        for(var i = 1; i < arr.length; i++) {
            if(arr[i - 1] > arr[i])
                continue;
            else return false;
        }
        return true;    
    }

    static int[] removeElementAtIndex(int[] arr, int index) {
        return IntStream.range(0, arr.length)
                .filter(j -> j != index)
                .map(j -> arr[j])
                .toArray();
    }
}