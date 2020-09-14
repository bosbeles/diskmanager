package record.view.table;

import record.repo.Bytes;
import record.repo.RecordMetaData;
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
        RecordMetaData metaData = recordRepo.get(row);
        if (metaData == null) {
            return null;
        }
        switch (col) {
            case 0:
                return metaData.getName();
            case 1:
                return metaData.getCreationTime();
            case 2:
                return metaData.getDuration();
            case 3:
                return metaData.getSize();
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

    public void addOrUpdate(RecordMetaData metaData) {
        recordRepo.add(metaData);
        fireTableDataChanged();
    }


    public boolean rename(String oldName, String newName) {
        RecordMetaData metaData = recordRepo.get(oldName);
        if (metaData != null) {
            RecordMetaData newMetaData = new RecordMetaData(metaData);
            newMetaData.setName(newName);
            return recordRepo.update(oldName, newMetaData);
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
