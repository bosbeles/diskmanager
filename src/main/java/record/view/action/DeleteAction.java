package record.view.action;

import record.view.table.RecordTableModel;
import record.view.RecordView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DeleteAction extends AbstractAction {

    private final RecordView view;

    public DeleteAction(RecordView view) {
        super("Delete");
        this.view = view;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        RecordTableModel model = (RecordTableModel) view.getRecordTable().getModel();
        int[] selectedRows = view.getRecordTable().getSelectedRows();
        if (selectedRows.length > 0) {
            int[] modelRows = new int[selectedRows.length];
            for (int i = 0; i < selectedRows.length; i++) {
                int selectedRow = selectedRows[i];
                int modelIndex = view.getRecordTable().convertRowIndexToModel(selectedRow);
                modelRows[i] = modelIndex;
            }
            model.remove(modelRows);
        }


    }
}
