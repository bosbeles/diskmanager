package record.view.table.renderer;

import record.util.FileUnit;
import record.util.FileSize;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ByteRenderer extends DefaultTableCellRenderer {


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (component instanceof JLabel) {
            ((JLabel) component).setHorizontalAlignment(RIGHT);
        }
        return component;
    }

    @Override
    protected void setValue(Object value) {
        Object result = value;
        if (value instanceof FileSize) {
            result = FileUnit.humanReadable(((FileSize) value).getSize());

        }

        setText(result == null ? "" : result.toString());
    }
}
