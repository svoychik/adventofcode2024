import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {
    
    public static void main(String[] args) throws Exception {
        var input = Files.readString(Paths.get(Day15.class.getResource("/day15.txt").toURI()));
        var chunks = input.split("\\n\\n");
        var gridLines = parseGridLines(chunks[0]);
        var steps = chunks.length > 1 ? chunks[1].replace("\n", "") : "";

        // Part 1
        var grid = initializeGrid(gridLines);
        var initialPos = findPosition(grid);
        processSteps(grid, initialPos, steps, false);
        var answer1 = calculateAnswer(grid, 'O');

        // Part 2
        grid.clear();
        var modifiedGridLines = modifyGridLines(gridLines);
        grid = initializeGrid(modifiedGridLines);
        initialPos = findPosition(grid);
        processSteps(grid, initialPos, steps, true);
        var answer2 = calculateAnswer(grid, '[');

        System.out.println(answer1);
        System.out.println(answer2);
    }


    private static List<String> parseGridLines(String gridChunk) {
        return Arrays.stream(gridChunk.split("\\n"))
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());
    }

    private static Grid initializeGrid(List<String> lines) {
        var grid = new Grid('O');
        for (var y = 0; y < lines.size(); y++) {
            var line = lines.get(y);
            for (var x = 0; x < line.length(); x++) {
                grid.setCell(new Position(x, y), line.charAt(x));
            }
        }
        return grid;
    }

    private static Position findPosition(Grid grid) {
        char target = '@';
        for (var entry : grid.entrySet()) {
            if (entry.getValue() == target) {
                return entry.getKey();
            }
        }
        return new Position(-1, -1); // Not found
    }

    private static List<String> modifyGridLines(List<String> originalLines) {
        List<String> modified = new ArrayList<>();
        for (var line : originalLines) {
            var sb = new StringBuilder();
            for (var ch : line.toCharArray()) {
                switch (ch) {
                    case '#':
                        sb.append("##");
                        break;
                    case 'O':
                        sb.append("[]");
                        break;
                    case '.':
                        sb.append("..");
                        break;
                    case '@':
                        sb.append("@.");
                        break;
                    default:
                        sb.append(ch);
                        break;
                }
            }
            modified.add(sb.toString());
        }
        return modified;
    }

    private static void processSteps(Grid grid, Position initialPos, String steps, boolean isPart2) {
        var pos = initialPos;
        for (var ins : steps.toCharArray()) {
            Direction dir;
            try {
                dir = Direction.fromSymbol(ins);
            } catch (IllegalArgumentException e) {
                continue;
            }

            boolean canMove;
            if (isPart2 && (dir == Direction.R || dir == Direction.L)) {
                canMove = canMoveInDirection(grid, pos, dir, true);
            } else {
                canMove = canMoveInDirection(grid, pos, dir, false);
            }

            if (canMove) {
                moveInDirection(grid, pos, dir, isPart2);
                pos = pos.move(dir);
            }
        }
    }

    private static boolean canMoveInDirection(Grid grid, Position pos, Direction dir, boolean isPart2) {
        if (isPart2 && (dir == Direction.R || dir == Direction.L)) {
            return canMoveHorizontal(grid, pos, dir);
        } else {
            return canMoveVertical(grid, pos, dir);
        }
    }

    private static boolean canMoveHorizontal(Grid grid, Position pos, Direction dir) {
        var next = pos.move(dir);
        var ch = grid.getCell(next);

        if (ch == '#') {
            return false;
        }
        if (ch == '.') {
            return true;
        }
        if (ch == 'O' || ch == '[' || ch == ']') {
            return canMoveHorizontal(grid, next, dir);
        }
        return false;
    }

    private static boolean canMoveVertical(Grid grid, Position pos, Direction dir) {
        var next = pos.move(dir);
        var ch = grid.getCell(next);

        if (ch == '#') {
            return false;
        }
        if (ch == '.') {
            return true;
        }
        if (ch == '[' || ch == ']') {
            var ddx = (ch == ']') ? -1 : 1;
            var pos1 = new Position(next.x() + ddx, next.y());
            var pos2 = new Position(next.x(), next.y());
            return canMoveVertical(grid, pos2, dir) && canMoveVertical(grid, pos1, dir);
        }
        return false;
    }

    private static void moveInDirection(Grid grid, Position pos, Direction dir, boolean isPart2) {
        if (isPart2 && (dir == Direction.R || dir == Direction.L)) {
            moveHorizontal(grid, pos, dir);
        } else {
            moveVertical(grid, pos, dir);
        }
    }

    private static void moveHorizontal(Grid grid, Position pos, Direction dir) {
        var next = pos.move(dir);
        var target = grid.getCell(next);

        if (target == 'O' || target == '[' || target == ']') {
            moveHorizontal(grid, next, dir);
        }

        if (grid.getCell(next) == '.') {
            grid.moveCharacter(pos, next);
        }
    }

    private static void moveVertical(Grid grid, Position pos, Direction dir) {
        var next = pos.move(dir);
        var target = grid.getCell(next);

        if (target == '[' || target == ']') {
            var ddx = (target == ']') ? -1 : 1;
            var pos1 = new Position(next.x() + ddx, next.y());
            var pos2 = new Position(next.x(), next.y());
            moveVertical(grid, pos2, dir);
            moveVertical(grid, pos1, dir);
        }

        if (grid.getCell(next) == '.') {
            grid.moveCharacter(pos, next);
        }
    }

    private static int calculateAnswer(Grid grid, char target) {
        return grid.entrySet().stream()
                .filter(entry -> entry.getValue() == target)
                .mapToInt(entry -> 100 * entry.getKey().y() + entry.getKey().x())
                .sum();
    }

    private enum Direction {
        V(0, 1, 'v'),
        R(1, 0, '>'),
        U(0, -1, '^'),
        L(-1, 0, '<');

        final int dx;
        final int dy;
        final char symbol;

        Direction(int dx, int dy, char symbol) {
            this.dx = dx;
            this.dy = dy;
            this.symbol = symbol;
        }

        static Direction fromSymbol(char symbol) {
            for (var dir : values()) {
                if (dir.symbol == symbol) {
                    return dir;
                }
            }
            throw new IllegalArgumentException("Invalid direction symbol: " + symbol);
        }

        static final List<Direction> ORDER = Arrays.asList(V, R, U, L);
    }

    private record Position(int x, int y) {
        Position move(Direction dir) {
            return new Position(this.x + dir.dx, this.y + dir.dy);
        }
    }

    private static class Grid {
        private final Map<Position, Character> cells = new HashMap<>();
        private final char defaultChar;

        Grid(char defaultChar) {
            this.defaultChar = defaultChar;
        }

        void setCell(Position pos, char ch) {
            cells.put(pos, ch);
        }

        char getCell(Position pos) {
            return cells.getOrDefault(pos, defaultChar);
        }

        void moveCharacter(Position from, Position to) {
            var ch = getCell(from);
            setCell(from, '.');
            setCell(to, ch);
        }

        Set<Map.Entry<Position, Character>> entrySet() {
            return cells.entrySet();
        }

        void clear() {
            cells.clear();
        }
    }

}
