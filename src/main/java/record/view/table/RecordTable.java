package record.view.table;

import record.repo.Bytes;
import record.view.table.renderer.ByteRenderer;
import record.view.table.renderer.DurationRenderer;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.time.Duration;

public class RecordTable extends JTable {


    public RecordTable() {
        setRowHeight(24);
        setAutoCreateRowSorter(true);
        setDefaultRenderer(Bytes.class, new ByteRenderer());
        setDefaultRenderer(Duration.class, new DurationRenderer());

    }

    public void refresh() {
        TableModel model = getModel();
        if (model instanceof RecordTableModel) {
            ((RecordTableModel) model).refresh();
        }
    }
}
