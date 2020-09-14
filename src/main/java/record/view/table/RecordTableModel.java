package record.view.table;

import record.util.Bytes;
import record.repo.RecordMetadata;
import record.repo.RecordRepo;

import javax.swing.table.AbstractTableModel;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class RecordTableModel extends AbstractTableModel {
    private final RecordRepo recordRepo;

    protected String[] columnNames = new String[]{
            "Name", "Creation Time", "Duration", "Size"
    };

    protected Class[] columnClasses = new Class[]{
            String.class, LocalDateTime.class, Duration.class,
            Bytes.class
    };

    // This table model works for any one given directory
    public RecordTableModel(RecordRepo recordRepo) {
        this.recordRepo = recordRepo;
    }

    // These are easy methods.
    public int getColumnCount() {
        return columnNames.length;
    }  // A constant for this model

    public int getRowCount() {
        return recordRepo.size();
    }  // # of files in dir

    // Information about each column.
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    // The method that must actually return the value of each cell.
    public Object getValueAt(int row, int col) {
        RecordMetadata metadata = recordRepo.get(row);
        if (metadata == null) {
            return null;
        }
        switch (col) {
            case 0:
                return metadata.getName();
            case 1:
                return metadata.getCreationTime();
            case 2:
                return metadata.getDuration();
            case 3:
                return metadata.getSize();
            default:
                return null;
        }
    }

    public void remove(int row) {
        Object obj = getValueAt(row, 0);
        if (obj instanceof String) {
            recordRepo.remove((String) obj);
        }
        fireTableRowsDeleted(row, row);
    }

    public void remove(int[] rowList) {
        Arrays.sort(rowList);
        for (int i = rowList.length - 1; i >= 0; i--) {
            Object obj = getValueAt(rowList[i], 0);
            if (obj instanceof String) {
                recordRepo.remove((String) obj);
            }
        }
        fireTableRowsDeleted(rowList[0], rowList[rowList.length - 1]);
    }

    public void addOrUpdate(RecordMetadata metadata) {
        recordRepo.add(metadata);
        fireTableDataChanged();
    }


    public boolean rename(String oldName, String newName) {
        RecordMetadata metadata = recordRepo.get(oldName);
        if (metadata != null) {
            RecordMetadata newMetadata = new RecordMetadata(metadata);
            newMetadata.setName(newName);
            return recordRepo.update(oldName, newMetadata);
        }
        return false;
    }

    public void refresh() {
        boolean changed = recordRepo.sync();
        if (changed) {
            fireTableDataChanged();
        }
    }

    public void refresh(boolean force) {
        boolean changed = recordRepo.sync(force);
        if (changed) {
            fireTableDataChanged();
        }
    }
}
