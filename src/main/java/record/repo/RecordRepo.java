package record.repo;

import org.apache.commons.io.FileUtils;

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

    private Map<String, RecordMetaData> records;
    private final File recordFolder;

    public RecordRepo(String path) {
        recordFolder = Paths.get(path).toFile();
        sync();
    }

    public int size() {
        return records.size();
    }

    public RecordMetaData get(int index) {
        Optional<String> first = records.keySet().stream().sorted().skip(index).findFirst();
        if (first.isPresent()) {
            return records.get(first.get());
        }
        return null;
    }

    public RecordMetaData get(String metaData) {
        return records.get(metaData);
    }

    public void add(RecordMetaData recordMetaData) {
        update(recordMetaData.getName(), recordMetaData);
    }

    public boolean remove(String name) {

        try {
            FileUtils.deleteDirectory(new File(recordFolder, name));
            RecordMetaData removed = records.remove(name);
            return removed != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(String name, RecordMetaData recordMetaData) {
        boolean result = true;
        if (!name.equals(recordMetaData.getName())) {
            File recordFile = new File(recordFolder, name);
            result = recordFile.renameTo(new File(recordFolder, recordMetaData.getName()));
            if (result) {
                records.remove(name);
            }
        }
        if (result) {
            records.put(recordMetaData.getName(), recordMetaData);
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
            RecordMetaData metaData = records.get(possibleRecord.getName());
            if (metaData == null) {
                metaData = readMetaData(possibleRecord);
                records.put(metaData.getName(), metaData);
                changed = true;
            }
        }
        boolean removed = records.entrySet().removeIf(e -> !possibleRecordNames.contains(e.getKey()));

        return changed || removed;
    }

    private RecordMetaData readMetaData(File possibleRecord) {
        RecordMetaData metaData = new RecordMetaData();
        metaData.setName(possibleRecord.getName());
        metaData.setSize(new Bytes(FileUtils.sizeOfDirectory(possibleRecord)));

        try (InputStream input = new FileInputStream(new File(possibleRecord, "stats.properties"))) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            String creationTime = prop.getProperty("creationTime");

            long ct = Long.parseLong(creationTime);
            metaData.setCreationTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(ct), ZoneId.systemDefault()));

            String duration = prop.getProperty("duration");
            long d = Long.parseLong(duration);
            metaData.setDuration(Duration.ofMillis(d));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return metaData;
    }
}
