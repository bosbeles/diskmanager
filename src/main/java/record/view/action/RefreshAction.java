package record.view.action;

import record.view.table.RecordTableModel;
import record.view.RecordView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RefreshAction extends AbstractAction {

    private final RecordView view;

    public RefreshAction(RecordView view) {
        super("Refresh");
        this.view = view;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        RecordTableModel model = (RecordTableModel) view.getRecordTable().getModel();
        model.refresh(true);
    }
}
