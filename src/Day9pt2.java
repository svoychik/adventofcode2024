import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.io.IOException;

public class Day9pt2 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var input = Files.readString(Paths.get(Day9pt2.class.getResource("/day9.txt").toURI())).trim();
        int[] disk = Day9.parseDiskMap(input);
        compactDiskWholeFiles(disk);
        long checksum = Day9.calculateChecksum(disk);
        System.out.println(checksum);
    }

    public static void compactDiskWholeFiles(int[] disk) {
        int maxFileID = -1;
        for (int block : disk) {
            if (block > maxFileID) {
                maxFileID = block;
            }
        }

        for (int fileID = maxFileID; fileID >= 0; fileID--) {
            int start = -1;
            int end = -1;
            for (int i = 0; i < disk.length; i++) {
                if (disk[i] == fileID) {
                    if (start == -1) start = i;
                    end = i;
                }
            }

            if (start == -1)
                continue;

            int fileLength = (end - start + 1);

            // scan from left to right for a contiguous run of free blocks
            int freeRunStart = -1;
            int freeRunLength = 0;
            int targetPosition = -1; // where we'll move the file if found

            for (int i = 0; i < start; i++) {
                if (disk[i] == -1) {
                    if (freeRunStart == -1) freeRunStart = i;
                    freeRunLength++;
                    if (freeRunLength >= fileLength) {
                        // a suitable run
                        targetPosition = freeRunStart;
                        break;
                    }
                } else {
                    // Reset run counting
                    freeRunStart = -1;
                    freeRunLength = 0;
                }
            }

            // If a suitable run is found, move the file
            if (targetPosition != -1) {
                for (int i = start; i <= end; i++) {
                    disk[i] = -1;
                }
                for (int i = 0; i < fileLength; i++) {
                    disk[targetPosition + i] = fileID;
                }
            }
        }
    }
}
