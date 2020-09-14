package org.example.monitor;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * <code>DiskSizeProvider</code> implementation.
 */
public class DiskSizeProviderImpl implements DiskSizeProvider {
    private final FileStore fileStore;


    public DiskSizeProviderImpl(String folder) {
        FileStore tempFileStore;
        Path path = Paths.get(folder);

        try {
            tempFileStore = Files.getFileStore(path);
        } catch (IOException e) {
            tempFileStore = null;
            e.printStackTrace();
        }


        fileStore = tempFileStore;
    }


    /**
     * Returns the size of disk.
     *
     * @return
     */
    @Override
    public long getUsedSize() {
        if (fileStore != null) {
            try {
                return fileStore.getTotalSpace() - fileStore.getUsableSpace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    @Override
    public long getTotalSize() {
        if (fileStore != null) {
            try {
                return fileStore.getTotalSpace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
