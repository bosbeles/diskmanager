package record.disk;

public interface DiskSizeProvider {

    long getUsedSize();

    long getTotalSize();

}
