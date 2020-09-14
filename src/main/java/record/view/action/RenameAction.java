package record.view.action;

import org.apache.commons.lang3.StringUtils;
import record.view.table.RecordTable;
import record.view.table.RecordTableModel;
import record.view.RecordView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class RenameAction extends AbstractAction {

    private final RecordView view;

    public RenameAction(RecordView view) {
        super("Rename");
        this.view = view;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        RecordTable recordTable = view.getRecordTable();
        RecordTableModel model = (RecordTableModel) recordTable.getModel();
        int[] selectedRows = recordTable.getSelectedRows();
        if (selectedRows.length > 0) {
            rename(recordTable, model, selectedRows);


        }


    }

    private void rename(RecordTable recordTable, RecordTableModel model, int[] selectedRows) {
        String initialValue = getInitialValue(recordTable, model, selectedRows);
        String suffix = (String) JOptionPane.showInputDialog(
                view,
                "Append a name for the record:",
                "Record Rename",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                initialValue);
        if (suffix != null) {
            suffix = StringUtils.isBlank(suffix) ? "" : "_" + suffix;
            boolean atLeastOneRename = false;
            Collection<String> newNames = new HashSet<>();
            for (int i = 0; i < selectedRows.length; i++) {
                int modelIndex = recordTable.convertRowIndexToModel(selectedRows[i]);
                String name = (String) model.getValueAt(modelIndex, 0);
                int suffixIndex = name.indexOf('_');
                String originalName;
                if (suffixIndex >= 0) {
                    originalName = name.substring(0, suffixIndex);
                } else {
                    originalName = name;
                }

                String newName = originalName + suffix;
                if (model.rename(name, newName)) {
                    atLeastOneRename = true;
                    newNames.add(newName);
                } else {
                    newNames.add(name);
                }
            }
            if (atLeastOneRename) {
                model.refresh(true);
                selectOldSelection(recordTable, model, newNames);
            }

        }
    }

    private void selectOldSelection(RecordTable recordTable, RecordTableModel model, Collection<String> newNames) {
        List<Integer> selectedRows = new ArrayList<>();
        int size = model.getRowCount();
        for (int i = 0; i < size; i++) {
            String name = (String) model.getValueAt(i, 0);
            if (newNames.contains(name)) {
                selectedRows.add(i);
            }
        }

        recordTable.getSelectionModel().clearSelection();
        selectedRows.stream().map(modelIndex -> recordTable.convertRowIndexToView(modelIndex)).forEach(viewIndex -> {
            recordTable.getSelectionModel().addSelectionInterval(viewIndex, viewIndex);
        });


    }

    private String getInitialValue(RecordTable recordTable, RecordTableModel model, int[] selectedRows) {
        String initialValue = "";
        if (selectedRows.length == 1) {
            int modelIndex = recordTable.convertRowIndexToModel(selectedRows[0]);
            Object name = model.getValueAt(modelIndex, 0);
            if (name instanceof String) {
                int suffixIndex = ((String) name).indexOf('_');
                if (suffixIndex >= 0) {
                    initialValue = ((String) name).substring(suffixIndex + 1);
                }
            }
        }
        return initialValue;
    }
}
