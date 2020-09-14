package record.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public final class FileSize implements Comparable<FileSize> {

    @NonNull
    private final long size;

    @Override
    public int compareTo(FileSize fs) {
        return Long.compare(size, fs.size);
    }
}
