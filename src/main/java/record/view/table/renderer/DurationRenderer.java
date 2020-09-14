package record.view.table.renderer;

import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.swing.table.DefaultTableCellRenderer;
import java.time.Duration;

public class DurationRenderer extends DefaultTableCellRenderer {


    @Override
    protected void setValue(Object value) {
        Object result = value;
        if (value instanceof Duration) {
            result = DurationFormatUtils.formatDuration(((Duration) value).toMillis(), "HH:mm:ss", true);
        }

        setText(result == null ? "" : result.toString());
    }
}
