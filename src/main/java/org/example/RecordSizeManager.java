package org.example;

import org.example.event.DiskFullEvent;
import org.example.event.FileSizeEvent;
import org.example.event.FolderFullEvent;
import org.example.monitor.FileSizeListener;
import org.example.monitor.MonitorManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class RecordSizeManager {

    private final MonitorManager monitorManager;
    private final String recordPath;

    private volatile long maxDiskSize;
    private volatile long maxRecordPathSize;
    private MonitorManager.Monitor recordPathMonitor;
    private MonitorManager.Monitor diskSizeMonitor;
    private FileSizeListenerImpl listener;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean destroyed = new AtomicBoolean(false);


    public RecordSizeManager(String recordPath, long maxDiskSize, long maxRecordPathSize) {
        this(new MonitorManager(), recordPath, maxDiskSize, maxRecordPathSize);

    }

    public RecordSizeManager(MonitorManager monitorManager, String recordPath, long maxDiskSize, long maxRecordPathSize) {
        this.monitorManager = monitorManager;
        this.recordPath = recordPath;
        this.listener = new FileSizeListenerImpl();
        this.maxDiskSize = maxDiskSize;
        this.maxRecordPathSize = maxRecordPathSize;
    }

    public void start() {
        if (destroyed.get()) {
            throw new IllegalStateException("Destroyed manager cannot be started for periodic change. Use a new manager instead.");
        }
        stop();
        started.set(true);
        createAndStartDiskSizeMonitor(maxDiskSize, true);
        createAndStartDiskSizeMonitor(maxRecordPathSize, false);
    }

    public void stop() {
        started.set(false);
        if (diskSizeMonitor != null) {
            diskSizeMonitor.stop();
        }
        if (recordPathMonitor != null) {
            recordPathMonitor.stop();
        }
    }

    public void destroy() {
        monitorManager.close();
        destroyed.set(true);
    }


    public long getMaxDiskSize() {
        return maxDiskSize;
    }

    public void setMaxDiskSize(long maxDiskSize) {
        this.maxDiskSize = maxDiskSize;
        if (started.get()) {
            if (diskSizeMonitor != null) {
                diskSizeMonitor.stop();
            }
            createAndStartDiskSizeMonitor(maxDiskSize, true);
        }
    }


    public long getMaxRecordPathSize() {
        return maxRecordPathSize;
    }

    public void setMaxRecordPathSize(long maxRecordPathSize) {
        this.maxRecordPathSize = maxRecordPathSize;
        if (started.get()) {
            if (recordPathMonitor != null) {
                recordPathMonitor.stop();
            }
            createAndStartDiskSizeMonitor(maxRecordPathSize, false);
        }
    }

    public boolean isRecordPathFull() {
        return recordPathMonitor.isFull();
    }

    public boolean isDiskFull() {
        return diskSizeMonitor.isFull(true);
    }


    private void createAndStartDiskSizeMonitor(long maxSize, boolean disk) {
        MonitorManager.Monitor monitor = this.monitorManager.createMonitor(recordPath, maxSize);
        monitor.addListener(listener);
        monitor.start(disk);
        if (disk) {
            diskSizeMonitor = monitor;
        } else {
            recordPathMonitor = monitor;
        }
    }


    private class FileSizeListenerImpl implements FileSizeListener {

        @Override
        public void onDiskEvent(FileSizeEvent event) {
            System.out.println("onDiskEvent: " + event);
            RecordSizeManager.this.stop();
            if (event instanceof FolderFullEvent) {
                // Folder is full
            } else if (event instanceof DiskFullEvent) {
                if (recordPathMonitor.isFull()) {
                    // Folder is full
                } else {
                    // Disk is full
                }
            } else {
                // Another event
            }
        }
    }
}
