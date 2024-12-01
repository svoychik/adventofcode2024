import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

public class Day1 {
    public static void main(String[] args) throws IOException {

        var lines = Files.readAllLines(Paths.get("resources/input.txt"));
        // pt1
        List<String[]> parsedValues = lines.stream().map(x -> (x.split("\\s+"))).toList(); 
        List<Integer> arr1 = parsedValues.stream().map(x -> Integer.parseInt(x[0])).sorted().toList();
        List<Integer> arr2 = parsedValues.stream().map(x -> Integer.parseInt(x[1])).sorted().toList();
        int res = IntStream.range(0, arr1.size()) 
                .map(i -> Math.abs(arr1.get(i) - arr2.get(i)))
                .sum();
        
        System.out.println(res);

        Map<Integer, Integer> freq = new Hashtable<>();
        
        for (Integer i : arr2) {
            freq.put(i, freq.getOrDefault(i, 0) + 1); 
        }
        int res2 = arr1.stream().map(x -> freq.containsKey(x) ? freq.get(x) * x : 0).reduce(0, Integer::sum);
        System.out.println(res2);
    }
}