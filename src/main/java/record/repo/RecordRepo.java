package record.repo;

import org.apache.commons.io.FileUtils;
import record.util.Bytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RecordRepo {

    private Map<String, RecordMetadata> records;
    private final File recordFolder;

    public RecordRepo(String path) {
        recordFolder = Paths.get(path).toFile();
        sync();
    }

    public int size() {
        return records.size();
    }

    public RecordMetadata get(int index) {
        Optional<String> first = records.keySet().stream().sorted().skip(index).findFirst();
        if (first.isPresent()) {
            return records.get(first.get());
        }
        return null;
    }

    public RecordMetadata get(String metadata) {
        return records.get(metadata);
    }

    public void add(RecordMetadata recordMetadata) {
        update(recordMetadata.getName(), recordMetadata);
    }

    public boolean remove(String name) {

        try {
            FileUtils.deleteDirectory(new File(recordFolder, name));
            RecordMetadata removed = records.remove(name);
            return removed != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(String name, RecordMetadata recordMetadata) {
        boolean result = true;
        if (!name.equals(recordMetadata.getName())) {
            File recordFile = new File(recordFolder, name);
            result = recordFile.renameTo(new File(recordFolder, recordMetadata.getName()));
            if (result) {
                records.remove(name);
            }
        }
        if (result) {
            records.put(recordMetadata.getName(), recordMetadata);
        }

        return result;
    }

    public synchronized boolean sync() {
        return sync(false);
    }

    public synchronized boolean sync(boolean recreate) {
        boolean changed = false;
        if (recreate || records == null) {
            records = new ConcurrentHashMap<>();
            changed = true;
        }
        File[] possibleRecords = recordFolder.listFiles(File::isDirectory);
        Arrays.sort(possibleRecords);
        Set<String> possibleRecordNames = Arrays.stream(possibleRecords).map(rec -> rec.getName()).collect(Collectors.toSet());
        for (File possibleRecord : possibleRecords) {
            RecordMetadata metadata = records.get(possibleRecord.getName());
            if (metadata == null) {
                metadata = readMetadata(possibleRecord);
                records.put(metadata.getName(), metadata);
                changed = true;
            }
        }
        boolean removed = records.entrySet().removeIf(e -> !possibleRecordNames.contains(e.getKey()));

        return changed || removed;
    }

    private RecordMetadata readMetadata(File possibleRecord) {
        RecordMetadata metadata = new RecordMetadata();
        metadata.setName(possibleRecord.getName());
        metadata.setSize(new Bytes(FileUtils.sizeOfDirectory(possibleRecord)));

        try (InputStream input = new FileInputStream(new File(possibleRecord, "stats.properties"))) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            String creationTime = prop.getProperty("creationTime");

            long ct = Long.parseLong(creationTime);
            metadata.setCreationTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(ct), ZoneId.systemDefault()));

            String duration = prop.getProperty("duration");
            long d = Long.parseLong(duration);
            metadata.setDuration(Duration.ofMillis(d));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return metadata;
    }
}
