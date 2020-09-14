package record.repo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiskStats {

    private final long usedSize;
    private final DiskParameters diskParameters;

    @Getter
    @AllArgsConstructor
    public static class DiskParameters {
        private final long totalSize;
        private final long warnSize;
        private final long errorSize;

    }
}
