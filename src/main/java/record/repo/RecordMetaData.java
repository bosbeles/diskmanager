package record.repo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordMetaData implements Comparable<RecordMetaData> {

    private String name;
    private LocalDateTime creationTime;
    private Duration duration;
    private Bytes size;
    private boolean locked = false;

    public RecordMetaData(RecordMetaData metaData) {
        this.name = metaData.name;
        this.creationTime = metaData.creationTime;
        this.duration = metaData.duration;
        this.size = metaData.size;
        this.locked = metaData.locked;
    }

    @Override
    public int compareTo(RecordMetaData o) {
        return name.compareTo(o.name);
    }
}
