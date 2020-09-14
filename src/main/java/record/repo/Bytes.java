package record.repo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Bytes implements Comparable<Bytes> {

    @Getter
    private final long bytes;

    public Bytes(long bytes) {
        this.bytes = bytes;
    }

    @Override
    public int compareTo(Bytes o) {
        return Long.compare(bytes, o.bytes);
    }
}
