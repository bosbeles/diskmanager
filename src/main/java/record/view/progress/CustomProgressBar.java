package record.view.progress;

import record.util.GBC;

import javax.swing.*;
import java.awt.*;

public class CustomProgressBar extends JPanel {

    public static final Color NORMAL = new Color(96, 167, 99);
    public static final Color WARN = new Color(244, 164, 48);
    public static final Color ERROR = new Color(232, 71, 63);

    private JProgressBar progressBar;
    private JLabel label;


    public CustomProgressBar() {
        setLayout(new GridBagLayout());

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(20, 2));
        label = new JLabel();

        this.add(progressBar, GBC.gbcHorizontal(0, 0));
        this.add(label, GBC.gbcHorizontal(0, 1));
    }


    public void setProgressColor(Color color) {
        progressBar.setForeground(color);
        label.setForeground(color);
    }

    public void setProgressText(String text) {
        label.setText(text);
    }

    public void setProgressValue(int value) {
        progressBar.setValue(value);
    }
}
