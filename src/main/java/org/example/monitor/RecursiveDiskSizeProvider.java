package org.example.monitor;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * A recursive directory traversal implementation of <code>DiskSizeProvider</code>
 */
public class RecursiveDiskSizeProvider implements DiskSizeProvider {
    private final File file;
    private final FileStore fileStore;


    public RecursiveDiskSizeProvider(String folder) {
        FileStore tempFileStore;
        Path path = Paths.get(folder);
        file = path.toFile();

        try {
            tempFileStore = Files.getFileStore(path);
        } catch (IOException e) {
            tempFileStore = null;
            e.printStackTrace();
        }


        fileStore = tempFileStore;
    }


    /**
     * Counts the size of a directory recursively (sum of the length of all files).
     *
     * @return size of directory in bytes
     */
    @Override
    public long getSizeOfFolder() {
        long time = System.currentTimeMillis();
        long size = FileUtils.sizeOfDirectory(file);
        time = System.currentTimeMillis() - time;
        //TODO Log
        System.out.println("Elapsed time: " + time + " ms.");
        return size;
    }

    /**
     * Returns the size of disk.
     *
     * @return
     */
    @Override
    public long getSizeofDisk() {
        if (fileStore != null) {
            try {
                return fileStore.getTotalSpace() - fileStore.getUsableSpace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }
}
