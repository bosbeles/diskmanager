package org.example;

import org.example.monitor.MonitorManager;
import org.example.monitor.MonitorManager.Monitor;

import java.util.concurrent.TimeUnit;

import static org.example.monitor.MonitorManager.FileUnit.GB;
import static org.example.monitor.MonitorManager.FileUnit.MB;

public class App {
    public static void main(String[] args) throws InterruptedException {
        final String recordPath = "D:\\Drivers\\Bluetooth";
        MonitorManager manager = new MonitorManager();

        Monitor monitor = manager.createMonitor(recordPath, MB.toByte(10));
        monitor.addListener(System.out::println);

        // Schedule periodic folder size check.
        // Folder size calculation is recursive, so it should not be used for deep folders.
        monitor.start();

        // Stops the monitor and its scheduler.
        // The monitor should not be reused for scheduling.
        // However it can be queried for isFull.
        monitor.stop();

        Monitor diskMonitor = manager.createMonitor(recordPath, GB.toByte(2));
        diskMonitor.addListener(System.out::println);
        // Start monitor for disk size instead of folder size.
        // Disk size is faster
        diskMonitor.start(true, 5, TimeUnit.SECONDS);

        // Monitor can be queried for its fullness. It will check the size once.
        // However the query may take several seconds especially for deep folders.
        manager.createMonitor("D:\\", GB.toByte(2))
                .isFull();


        TimeUnit.SECONDS.sleep(15);

        manager.close();

        System.out.println("Testing record size manager.");
        RecordSizeManager recordSizeManager = new RecordSizeManager(recordPath, GB.toByte(2), MB.toByte(10));

        // After starting a record, check for the sizes.
        if (recordSizeManager.isRecordPathFull() || recordSizeManager.isDiskFull()) {
            // Do not start a record.
        }

        // After record started, start the periodic checks on size manager.
        recordSizeManager.start();
        TimeUnit.SECONDS.sleep(15);
        // When one of the checks detects overflow in size, record size manager will be stopped.
        // In that case you should be stopping the record.
        // Put a listener to record size manager and then stop the record.
        System.out.println("Test Finished.");

        // Destroy will destroy the periodic change thread.
        recordSizeManager.destroy();

    }
}
