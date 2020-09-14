package record.view;

import lombok.Getter;
import record.disk.DiskSizeProviderImpl;
import record.repo.DiskStats;
import record.repo.RecordOrder;
import record.repo.RecordRepo;
import record.test.GuiTester;
import record.util.FileUnit;
import record.view.action.RecordAction;
import record.view.progress.DiskSpaceProgressBar;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static record.util.GBC.gbc;
import static record.util.GBC.gbcHorizontal;

public class RecordTestPanel extends JPanel {


    @Getter
    private JButton recordButton;
    @Getter
    private JProgressBar progressBar;
    @Getter
    private JProgressBar timesProgressBar;
    @Getter
    private RecordView recordView;

    private JTextField recordField;
    private JComboBox<FileUnit> unitComboBox;


    private JTextField timesField;


    private DiskSpaceProgressBar diskUsage;
    private final transient DiskSizeProviderImpl sizeProvider;
    private final transient DiskStats.DiskParameters parameters;
    private final transient ScheduledExecutorService scheduler;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    public RecordTestPanel() {

        scheduler = Executors.newSingleThreadScheduledExecutor();

        init();

        sizeProvider = new DiskSizeProviderImpl("D:\\Records");
        long totalSize = sizeProvider.getTotalSize();
        parameters = new DiskStats.DiskParameters(totalSize, (long) (totalSize * 0.80), (long) (totalSize * 0.90));

        scheduler.scheduleWithFixedDelay(this::updateDiskStatus, 0, 10, TimeUnit.SECONDS);

    }

    private void init() {
        setLayout(new BorderLayout());

        recordField = new JTextField("100");

        unitComboBox = new JComboBox<>(FileUnit.values());
        unitComboBox.setSelectedItem(FileUnit.MB);

        timesField = new JTextField("1");

        recordButton = new JButton("Record");
        recordButton.setAction(new RecordAction(this));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(20, 2));
        progressBar.setVisible(false);

        timesProgressBar = new JProgressBar();
        timesProgressBar.setPreferredSize(new Dimension(20, 2));
        timesProgressBar.setVisible(false);

        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(recordField, gbcHorizontal(0, 0));
        panel.add(unitComboBox, gbc(1, 0));
        panel.add(timesField, gbc(2, 0));
        panel.add(recordButton, gbc(3, 0));
        panel.add(progressBar, gbcHorizontal(0, 1));
        GridBagConstraints timeProgressGbc = gbcHorizontal(2, 1);
        timeProgressGbc.weightx = 0.0;
        panel.add(timesProgressBar, timeProgressGbc);


        JPanel statusBar = new JPanel(new GridBagLayout());

        diskUsage = new DiskSpaceProgressBar();
        diskUsage.setVisible(false);


        statusBar.add(new JSeparator(), gbcHorizontal(0, 0));

        GridBagConstraints diskUsageGbc = gbc(0, 1);
        diskUsageGbc.anchor = GridBagConstraints.EAST;
        diskUsageGbc.insets = new Insets(5, 5, 5, 5);
        statusBar.add(diskUsage, diskUsageGbc);

        recordView = new RecordView(new RecordRepo("D:\\Records"));
        scheduler.scheduleWithFixedDelay(() ->
                        EventQueue.invokeLater(() ->
                                recordView.getRecordTable().refresh()),
                10, 10, TimeUnit.SECONDS);


        add(recordView);
        add(panel, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);
    }

    public List<RecordOrder> getRecordOrderList() {
        int times = Integer.parseInt(timesField.getText());
        List<RecordOrder> list = new ArrayList<>();


        double d = Double.parseDouble(recordField.getText());
        FileUnit unit = unitComboBox.getItemAt(unitComboBox.getSelectedIndex());
        long bytes = unit.toByte(d);
        String name = dateFormat.format(new Date());

        for (int i = 0; i < times; i++) {
            RecordOrder.RecordOrderBuilder recordOrderBuilder = RecordOrder.builder().bytes(bytes);
            if (i > 0) {
                recordOrderBuilder.name(name + "_(" + (i + 1) + ")");
            } else {
                recordOrderBuilder.name(name);
            }
            list.add(recordOrderBuilder.build());

        }


        return list;

    }

    public void onDiskStats(DiskStats stats) {
        diskUsage.setVisible(true);
        diskUsage.updateDiskStats(stats);
    }

    public synchronized void updateDiskStatus() {
        long usedSize = sizeProvider.getUsedSize();
        final DiskStats stats = new DiskStats(usedSize, parameters);
        EventQueue.invokeLater(() -> onDiskStats(stats));
    }

    public static void main(String[] args) {

        GuiTester.test(frame -> new RecordTestPanel(), "FlatDark");
    }


}
