package record.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public final class Bytes implements Comparable<Bytes> {

    @NonNull
    private final long bytes;

    @Override
    public int compareTo(Bytes o) {
        return Long.compare(bytes, o.bytes);
    }
}
