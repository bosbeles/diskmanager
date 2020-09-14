package org.example.monitor;

public interface DiskSizeProvider {

    long getUsedSize();

    long getTotalSize();

}
