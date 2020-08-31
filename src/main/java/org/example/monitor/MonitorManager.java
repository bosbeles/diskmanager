package org.example.monitor;

import org.example.event.DiskFullEvent;
import org.example.event.FileSizeEvent;
import org.example.event.FolderFullEvent;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class MonitorManager {

    private final Function<String, DiskSizeProvider> diskSizeProviderFactory;
    private ScheduledExecutorService scheduler;
    private List<Monitor> monitorList;

    /**
     * Creates a folder size monitoring manager.
     */
    public MonitorManager() {
        this(path -> new RecursiveDiskSizeProvider(path));
    }

    /**
     * Creates a folder size monitoring manager using custom factory of provider for fetching folder size.
     *
     * @param diskSizeProviderFactory a factory that creates a folder size provider.
     */
    public MonitorManager(Function<String, DiskSizeProvider> diskSizeProviderFactory) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setRemoveOnCancelPolicy(true);
        this.scheduler = Executors.unconfigurableScheduledExecutorService(executor);
        this.monitorList = new CopyOnWriteArrayList<>();
        this.diskSizeProviderFactory = diskSizeProviderFactory;
    }


    /**
     * Create a file (leaf life or directory) size monitor.
     *
     * @param path    the file or directory
     * @param maxSize maxSize for the file or directory.
     * @return
     */
    public Monitor createMonitor(String path, long maxSize) {
        DiskSizeProvider sizeProvider = diskSizeProviderFactory.apply(path);
        Monitor monitor = new Monitor(sizeProvider, maxSize);
        return monitor;
    }

    /**
     * Closes the given monitor. Stops the schedulers.
     *
     * @param monitor the monitor to be closed.
     */
    public void close(Monitor monitor) {
        monitor.stop();
    }

    /**
     * Closes the monitor manager and its
     */
    public void close() {
        monitorList.forEach(Monitor::stop);
        monitorList.clear();
        scheduler.shutdownNow();
    }

    /**
     * Enumeration for File units.
     */
    public enum FileUnit {
        GB(1024 * 1024 * 1024),
        MB(1024 * 1024),
        KB(1024),
        B(1);

        private long multiplier;

        private FileUnit(long multiplier) {
            this.multiplier = multiplier;
        }

        public long getMultiplier() {
            return multiplier;
        }

        /**
         * Converts to length in bytes.
         *
         * @param n the number
         * @return the length in bytes.
         */
        public long toByte(long n) {
            return n * multiplier;
        }
    }


    /**
     * A path size monitor.
     */
    public class Monitor {

        private long maxSize;
        private ScheduledFuture<?> scheduledFuture;
        private DiskSizeProvider sizeProvider;

        private List<FileSizeListener> listenerList = new CopyOnWriteArrayList<>();
        private AtomicBoolean diskFull = new AtomicBoolean(false);
        private volatile boolean alreadyStarted;


        /**
         * @param sizeProvider
         * @param maxSize
         */
        private Monitor(DiskSizeProvider sizeProvider, final long maxSize) {
            this.maxSize = maxSize;
            this.sizeProvider = sizeProvider;
        }

        public void start() {
            start(false);
        }

        public void start(boolean disk) {
            start(disk, 10, TimeUnit.SECONDS);
        }

        public void start(long pollTime, TimeUnit timeUnit) {
            start(false, pollTime, timeUnit);
        }

        public void start(boolean disk, long pollTime, TimeUnit timeUnit) {
            if (!alreadyStarted) {
                scheduledFuture = scheduler.scheduleWithFixedDelay(() -> {
                    long sizeOfDisk = disk ? sizeProvider.getSizeofDisk() : sizeProvider.getSizeOfFolder();

                    boolean diskFullFlag = sizeOfDisk >= maxSize;

                    boolean generateDiskFullEvent = diskFullFlag && diskFull.compareAndSet(!diskFullFlag, diskFullFlag);
                    if (generateDiskFullEvent) {
                        FileSizeEvent event = disk ? new DiskFullEvent(maxSize, sizeOfDisk) : new FolderFullEvent(maxSize, sizeOfDisk);
                        listenerList.forEach(listener -> listener.onDiskEvent(event));
                    }
                }, 0, pollTime, timeUnit);
                alreadyStarted = true;
            }
            monitorList.add(this);
        }


        public boolean isFull() {
            return isFull(false);
        }

        public boolean isFull(boolean disk) {
            long used = disk ? sizeProvider.getSizeofDisk() : sizeProvider.getSizeOfFolder();
            return used >= maxSize;
        }


        public Monitor addListener(FileSizeListener listener) {
            listenerList.add(listener);
            return this;
        }

        public Monitor removeListener(FileSizeListener listener) {
            listenerList.remove(listener);
            return this;
        }

        public void stop() {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            monitorList.remove(this);
            listenerList.clear();
        }
    }

}
