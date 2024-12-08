import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day5v2 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day5v2.class.getResource("/day5.txt").toURI()));
        String[] sections = input.split("\n\n");
        Map<Integer, List<Integer>> orderingRules = Day5.parseRules((sections[0].split("\n")));
        var updates = Day5.parseUpdates(sections[1].split("\n"));

        int totalPart1 = 0;
        List<List<Integer>> invalidUpdates = new ArrayList<>();

        for (var update : updates) {
            if (Day5.isValidUpdate(update, orderingRules)) {
                totalPart1 += Day5.getMiddlePage(update);
            } else {
                invalidUpdates.add(new ArrayList<>(update));
            }
        }

        System.out.println("Sum of middle pages (Part 1): " + totalPart1);

        int totalPart2 = 0;

        for (List<Integer> invalidUpdate : invalidUpdates) {
            List<Integer> fixedUpdate = fixUpdate(orderingRules, invalidUpdate);
            totalPart2 += Day5.getMiddlePage(fixedUpdate);
        }

        System.out.println("Sum of middle pages after fixing (Part 2): " + totalPart2);
    }

    public static List<Integer> fixUpdate(Map<Integer, List<Integer>> orderMap, List<Integer> update) {
        boolean swappedPages;
        
        do {
            swappedPages = false;

            for (int i = 0; i < update.size(); i++) {
                var x = update.get(i);
                List<Integer> precedeList = orderMap.getOrDefault(x, new ArrayList<>());
                for (Integer y : precedeList) {
                    //check every element of x                    
                    int pageIndex = update.indexOf(y);
                    if (pageIndex != -1 && pageIndex < i) {
                        // Swap pages to fix order
                        int temp = update.get(pageIndex);
                        update.set(pageIndex, x);
                        update.set(i, temp);
                        swappedPages = true;
                    }
                }
            }
        } while (swappedPages);

        return update;
    }
}
