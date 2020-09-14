package record.view;

import lombok.Getter;
import record.repo.RecordRepo;
import record.test.GuiTester;
import record.view.action.DeleteAction;
import record.view.action.RefreshAction;
import record.view.action.RenameAction;
import record.view.table.RecordTable;
import record.view.table.RecordTableModel;

import javax.swing.*;
import java.awt.*;

import static record.util.GBC.gbc;
import static record.util.GBC.gbcHorizontal;

public class RecordView extends JPanel {

    @Getter
    private final transient RecordRepo recordRepo;
    @Getter
    private RecordTable recordTable;


    public RecordView(RecordRepo recordRepo) {
        this.recordRepo = recordRepo;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());

        recordTable = new RecordTable();
        RecordTableModel dataModel = new RecordTableModel(recordRepo);
        recordTable.setModel(dataModel);
        JScrollPane scrollPane = new JScrollPane(recordTable);
        recordTable.setFillsViewportHeight(true);

        JButton refreshButton = new JButton(new RefreshAction(this));
        Dimension size = refreshButton.getPreferredSize();
        size.width = 100;
        refreshButton.setPreferredSize(size);
        JButton renameButton = new JButton(new RenameAction(this));
        JButton deleteButton = new JButton(new DeleteAction(this));


        GridBagConstraints recordTableGbc = gbc(0, 0);
        recordTableGbc.gridheight = 4;
        recordTableGbc.fill = GridBagConstraints.BOTH;
        recordTableGbc.weightx = 1.0;

        add(scrollPane, recordTableGbc);
        add(refreshButton, gbcHorizontal(1, 0, 0.0));
        add(renameButton, gbcHorizontal(1, 1, 0.0));
        add(deleteButton, gbcHorizontal(1, 2, 0.0));
        add(Box.createVerticalGlue(), gbc(1, 3, GridBagConstraints.VERTICAL));

    }


    public static void main(String[] args) {
        GuiTester.test(f -> new RecordView(new RecordRepo("D:\\Records")));
    }

}
