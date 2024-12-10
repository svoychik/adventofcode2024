import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.io.IOException;

public class Day9 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day9.class.getResource("/day9.txt").toURI())).trim();
        int[] disk = parseDiskMap(input);
        compactDisk(disk);
        long checksum = calculateChecksum(disk);
        System.out.println(checksum);
    }

    public static int[] parseDiskMap(String input) {
        int totalLength = 0;
        for (int i = 0; i < input.length(); i++) {
            int length = Character.getNumericValue(input.charAt(i));
            totalLength += length;
        }

        int[] disk = new int[totalLength];
        int fileID = 0;
        int pos = 0;
        for (int i = 0; i < input.length(); i++) {
            int length = Character.getNumericValue(input.charAt(i));
            if (i % 2 == 0) {
                // File blocks
                for (int j = 0; j < length; j++) {
                    disk[pos++] = fileID;
                }
                fileID++;
            } else {
                // Free blocks
                for (int j = 0; j < length; j++) {
                    disk[pos++] = -1;
                }
            }
        }
        return disk;
    }

    public static void compactDisk(int[] disk) {
        while (true) {
            int leftmostFree = findLeftmostFree(disk);
            if (leftmostFree == -1) break; // no free space

            int rightmostFile = findRightmostFile(disk);
            if (rightmostFile == -1) break; // no files

            if (leftmostFree < rightmostFile) {
                disk[leftmostFree] = disk[rightmostFile];
                disk[rightmostFile] = -1;
            } else {
                break;
            }
        }
    }

    public static long calculateChecksum(int[] disk) {
        long checksum = 0;
        for (int i = 0; i < disk.length; i++) {
            if (disk[i] != -1) {
                checksum += (long) i * disk[i];
            }
        }
        return checksum;
    }

    public static int findLeftmostFree(int[] disk) {
        for (int i = 0; i < disk.length; i++) {
            if (disk[i] == -1) return i;
        }
        return -1;
    }

    public static int findRightmostFile(int[] disk) {
        for (int i = disk.length - 1; i >= 0; i--) {
            if (disk[i] != -1) return i;
        }
        return -1;
    }
}
