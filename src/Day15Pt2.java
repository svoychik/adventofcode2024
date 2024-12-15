import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Part Two Solution for Day15: Warehouse Woes
 *
 * This program scales up the warehouse map, processes robot movements, and calculates
 * the sum of all boxes' GPS coordinates after all movements are executed.
 */
public class Day15Pt2 {

    public static void main(String[] args) throws Exception {
        // Read the input file
        var input = Files.readString(Paths.get(Day15Pt2.class.getResource("/day15.txt").toURI()));
        String[] lines = input.split("\\r?\\n");

        // Separate map lines and move lines
        List<String> mapOriginal = new ArrayList<>();
        List<String> moveLines = new ArrayList<>();
        boolean mapEnded = false;
        for (String line : lines) {
            if (!mapEnded && (line.contains("#") || line.contains("O") || line.contains("@") || line.contains("."))) {
                mapOriginal.add(line);
            } else {
                // Assuming that move instructions do not contain any of the map characters
                if (!line.trim().isEmpty()) {
                    moveLines.add(line.trim());
                }
                mapEnded = true;
            }
        }

        // Scale up the map
        List<String> scaledMap = new ArrayList<>();
        for (String line : mapOriginal) {
            StringBuilder scaledLine = new StringBuilder();
            for (char ch : line.toCharArray()) {
                switch (ch) {
                    case '#':
                        scaledLine.append("##");
                        break;
                    case 'O':
                        scaledLine.append("[]");
                        break;
                    case '.':
                        scaledLine.append("..");
                        break;
                    case '@':
                        scaledLine.append("@.");
                        break;
                    default:
                        // Handle unexpected characters by scaling them as empty floor tiles
                        scaledLine.append("..");
                        break;
                }
            }
            scaledMap.add(scaledLine.toString());
        }

        // Convert scaled map to 2D char array for easy manipulation
        int numRows = scaledMap.size();
        int numCols = scaledMap.get(0).length();
        char[][] warehouse = new char[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            warehouse[r] = scaledMap.get(r).toCharArray();
        }

        // Find robot's initial position
        int robotRow = -1, robotCol = -1;
        outerLoop:
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (warehouse[r][c] == '@') {
                    robotRow = r;
                    robotCol = c;
                    break outerLoop;
                }
            }
        }

        if (robotRow == -1 || robotCol == -1) {
            System.err.println("Robot (@) not found in the map.");
            return;
        }

        // Concatenate all move lines into a single move string
        StringBuilder movesBuilder = new StringBuilder();
        for (String moveLine : moveLines) {
            movesBuilder.append(moveLine.trim());
        }
        String moves = movesBuilder.toString().replaceAll("\\s+", "");

        // Define movement directions
        // '^' = up (-1,0), 'v' = down (1,0), '<' = left (0,-1), '>' = right (0,1)
        int[] dr = new int[256];
        int[] dc = new int[256];
        dr['^'] = -1; dc['^'] = 0;
        dr['v'] = 1; dc['v'] = 0;
        dr['<'] = 0; dc['<'] = -1;
        dr['>'] = 0; dc['>'] = 1;

        // Process each move
        for (char move : moves.toCharArray()) {
            if (move != '^' && move != 'v' && move != '<' && move != '>') {
                // Ignore invalid move characters
                continue;
            }

            int deltaRow = dr[move];
            int deltaCol = dc[move];

            int targetRow = robotRow + deltaRow;
            int targetCol = robotCol + deltaCol;

            // Check boundaries
            if (targetRow < 0 || targetRow >= numRows || targetCol < 0 || targetCol >= numCols) {
                // Move is out of bounds; skip
                continue;
            }

            // Determine what's in the target cell
            char targetCell = warehouse[targetRow][targetCol];
            char targetNextCell = '\0'; // Next cell after the target (used when pushing boxes)

            if (targetCell == '.') {
                // Move robot to the empty cell
                warehouse[robotRow][robotCol] = '.'; // Previous cell becomes empty
                robotRow = targetRow;
                robotCol = targetCol;
                warehouse[robotRow][robotCol] = '@'; // Place robot
            } else if (targetCell == '[') {
                // Check if it's a box '[]'
                if (targetCol + 1 < numCols && warehouse[targetRow][targetCol + 1] == ']') {
                    // Attempt to push the box
                    int pushRow = targetRow + deltaRow;
                    int pushCol = targetCol + deltaCol;

                    // Check if the position to push is within bounds
                    if (pushRow < 0 || pushRow >= numRows || pushCol < 0 || pushCol + 1 >= numCols) {
                        // Cannot push out of bounds
                        continue;
                    }

                    // Check what's in the position to push
                    targetNextCell = warehouse[pushRow][pushCol];
                    char targetNextNextCell = warehouse[pushRow][pushCol + 1];

                    if (targetNextCell == '.' && targetNextNextCell == '.') {
                        // Push is possible; move the box
                        warehouse[pushRow][pushCol] = '[';
                        warehouse[pushRow][pushCol + 1] = ']';
                        // Clear the old box position
                        warehouse[targetRow][targetCol] = '.';
                        warehouse[targetRow][targetCol + 1] = '.';

                        // Move the robot
                        warehouse[robotRow][robotCol] = '.'; // Previous cell becomes empty
                        robotRow = targetRow;
                        robotCol = targetCol;
                        warehouse[robotRow][robotCol] = '@'; // Place robot
                    }
                    // Else, push is blocked; do nothing
                }
            }
            // If target cell is not '.' or '[', it's a wall or part of a box; do nothing
        }

        // After all moves, calculate the sum of GPS coordinates of all boxes
        long sumGPS = 0;                            for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (warehouse[r][c] == '[') {
                    // Each box is represented by '[' at (r, c) and ']' at (r, c+1)
                    // GPS is based on the '[' position
                    sumGPS += (r * 100) + c;
                }
            }
        }

        // Output the result
        System.out.println(sumGPS);
    }
}
