import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day5v2 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day5.class.getResource("/day5.txt").toURI()));
        String[] lines = input.split("\n");

        Map<Integer, List<Integer>> orderMap = Day5.parseRules(lines);
        List<List<Integer>> updates = Day5.parseUpdates(lines);

        int totalPart1 = 0;
        List<List<Integer>> invalidUpdates = new ArrayList<>();

        for (List<Integer> update : updates) {
            if (Day5.isValidUpdate(update, orderMap)) {
                totalPart1 += Day5.getMiddlePage(update);
            } else {
                invalidUpdates.add(update);
            }
        }

        System.out.println("Sum of middle pages (Part 1): " + totalPart1);

        int totalPart2 = 0;

        for (List<Integer> invalidUpdate : invalidUpdates) {
            List<Integer> fixedUpdate = fixUpdate(orderMap, invalidUpdate);
            totalPart2 += Day5.getMiddlePage(fixedUpdate);
        }

        System.out.println("Sum of middle pages after fixing (Part 2): " + totalPart2);
    }

    public static List<Integer> fixUpdate(Map<Integer, List<Integer>> orderMap, List<Integer> update) {
        boolean swappedPages;

        do {
            swappedPages = false;

            for (int i = 0; i < update.size(); i++) {
                List<Integer> precedeList = orderMap.getOrDefault(update.get(i), new ArrayList<>());

                for (Integer page : precedeList) {
                    int pageIndex = update.indexOf(page);
                    if (pageIndex != -1 && pageIndex < i) {
                        // Swap pages to fix order
                        int temp = update.get(pageIndex);
                        update.set(pageIndex, update.get(i));
                        update.set(i, temp);
                        swappedPages = true;
                    }
                }
            }
        } while (swappedPages);

        return update;
    }
}
