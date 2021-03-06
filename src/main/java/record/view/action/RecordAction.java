package record.view.action;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import record.repo.RecordMetadata;
import record.repo.RecordOrder;
import record.util.FileSize;
import record.view.RecordTestPanel;
import record.view.table.RecordTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.IntConsumer;

@Log4j2
public class RecordAction extends AbstractAction {


    private static final byte[] FIXED_BYTES = new byte[4096];
    private final RecordTestPanel frame;

    public RecordAction(RecordTestPanel frame) {
        super("Record");
        this.frame = frame;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        List<RecordOrder> list = frame.getRecordOrderList();
        final RecordOrder[] recordOrderList = list.toArray(new RecordOrder[list.size()]);


        frame.getProgressBar().setValue(0);
        frame.getProgressBar().setVisible(true);
        frame.getTimesProgressBar().setValue(0);
        frame.getTimesProgressBar().setVisible(true);
        frame.getRecordButton().setEnabled(false);
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                IntConsumer progressUpdater = this::setProgress;
                for (int i = 0; i < recordOrderList.length; i++) {
                    RecordOrder recordOrder = recordOrderList[i];
                    final RecordMetadata metadata = new RecordMetadata();
                    metadata.setName(recordOrder.getName());
                    metadata.setLocked(true);
                    updateMetadataInGui(metadata);
                    RecordMetadata resultMetadata = processRecordOrder(recordOrder, progressUpdater);

                    updateMetadataInGui(resultMetadata);

                    publish(i + 1);
                }

                return null;
            }


            @Override
            protected void process(List<Integer> chunks) {
                for (Integer val : chunks) {
                    frame.getTimesProgressBar().setValue(val * 100 / recordOrderList.length);
                }

            }

            @Override
            protected void done() {
                setProgress(100);
                frame.getTimesProgressBar().setValue(100);
                frame.getRecordButton().setEnabled(true);
                frame.getProgressBar().setVisible(false);
                frame.getTimesProgressBar().setVisible(false);
                frame.updateDiskStatus();
                try {
                    get();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                }
            }
        };
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                frame.getProgressBar().setValue((Integer) evt.getNewValue());
            }
        });
        worker.execute();


    }

    private void updateMetadataInGui(final RecordMetadata metadata) {
        EventQueue.invokeLater(() -> {
            RecordTableModel model = (RecordTableModel) frame.getRecordView().getRecordTable().getModel();
            model.addOrUpdate(metadata);
        });
    }

    private RecordMetadata processRecordOrder(RecordOrder recordOrder, IntConsumer progressUpdater) throws IOException {
        final long bytes = recordOrder.getBytes();
        final String recordName = recordOrder.getName();


        long longBytesRemaining = bytes;

        Path recordPath = Paths.get("D:", "Records", recordName);
        File currentRecordFolder = recordPath.toFile();
        currentRecordFolder.mkdirs();

        try (FileOutputStream stream = new FileOutputStream(new File(currentRecordFolder, "database.ocf"))) {
            while (longBytesRemaining > 0) {
                byte[] written = FIXED_BYTES;
                if (longBytesRemaining < 4096) {
                    written = new byte[(int) longBytesRemaining];
                }
                stream.write(written);
                longBytesRemaining -= written.length;
                progressUpdater.accept((int) ((bytes - longBytesRemaining) * 100 / bytes));
            }


        } catch (IOException e) {
            log.debug("Record {} could not be written.", currentRecordFolder.getName(), e);
            throw e;
        }

        RecordMetadata metadata = new RecordMetadata();
        metadata.setName(recordName);
        metadata.setLocked(false);

        try (OutputStream output = new FileOutputStream(new File(currentRecordFolder, "stats.properties"))) {

            Properties prop = new Properties();

            // set the properties value

            Instant now = Instant.now();
            Duration duration = Duration.ofMillis(bytes / 100);
            metadata.setCreationTime(LocalDateTime.ofInstant(now, ZoneId.systemDefault()));
            metadata.setDuration(duration);


            prop.setProperty("creationTime", Long.toString(now.toEpochMilli()));
            prop.setProperty("duration", Long.toString(duration.toMillis()));


            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
        metadata.setSize(new FileSize(FileUtils.sizeOfDirectory(currentRecordFolder)));

        return metadata;
    }
}
