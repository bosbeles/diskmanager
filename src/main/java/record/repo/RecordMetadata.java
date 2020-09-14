package record.repo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import record.util.FileSize;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordMetadata implements Comparable<RecordMetadata> {

    private String name;
    private LocalDateTime creationTime;
    private Duration duration;
    private FileSize size;
    private boolean locked = false;

    public RecordMetadata(RecordMetadata metadata) {
        this.name = metadata.name;
        this.creationTime = metadata.creationTime;
        this.duration = metadata.duration;
        this.size = metadata.size;
        this.locked = metadata.locked;
    }

    @Override
    public int compareTo(RecordMetadata o) {
        return name.compareTo(o.name);
    }
}
