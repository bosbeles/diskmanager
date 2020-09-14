package record.util;

import java.awt.*;

public class GBC {

    private GBC() {
        // Hide constructor.
    }

    public static GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.gridx = x;
        gbc.gridy = y;

        return gbc;
    }

    public static GridBagConstraints gbc(int x, int y, int fill) {
        return gbc(x, y, fill, 1.0);
    }

    public static GridBagConstraints gbc(int x, int y, int fill, double weight) {
        GridBagConstraints gbc = gbc(x, y);
        gbc.fill = fill;
        switch (fill) {
            case GridBagConstraints.BOTH:
                gbc.weightx = weight;
                gbc.weighty = weight;
                break;
            case GridBagConstraints.HORIZONTAL:
                gbc.weightx = weight;
                break;
            case GridBagConstraints.VERTICAL:
                gbc.weighty = weight;
                break;
            default:
                break;
        }

        return gbc;
    }


    public static GridBagConstraints gbcHorizontal(int x, int y) {
        return gbc(x, y, GridBagConstraints.HORIZONTAL);
    }

    public static GridBagConstraints gbcHorizontal(int x, int y, double weight) {
        return gbc(x, y, GridBagConstraints.HORIZONTAL, weight);
    }

    public static GridBagConstraints gbcHorizontal(int x, int y, int w) {
        GridBagConstraints gbc = gbc(x, y, GridBagConstraints.HORIZONTAL);
        gbc.gridwidth = w;
        return gbc;
    }
}
