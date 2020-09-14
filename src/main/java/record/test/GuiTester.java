package record.test;

import com.formdev.flatlaf.extras.FlatInspector;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuiTester {
    public static void test(JPanel panel) {
        test(frame -> panel, null);
    }

    public static void test(Function<JFrame, JComponent> panelSupplier) {
        test(panelSupplier, "Nimbus");
    }

    public static void test(Function<JFrame, JComponent> panelSupplier, String lookAndFeel) {
        test(panelSupplier, lookAndFeel, f -> {
        });
    }

    public static void test(Function<JFrame, JComponent> panelSupplier, String lookAndFeel, Consumer<JFrame> frameConsumer) {
        EventQueue.invokeLater(() -> testPrivate(panelSupplier, lookAndFeel, frameConsumer));


    }

    private static void testPrivate(Function<JFrame, JComponent> panelSupplier, String lookAndFeel, Consumer<JFrame> frameConsumer) {

        FlatInspector.install("ctrl shift alt X");
        // Get all the available look and feel that we are going to use for
        // creating the JMenuItem and assign the action listener to handle
        // the selection of menu item to change the look and feel.

        String[] lafs = {
                "org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeels",
                "org.pushingpixels.substance.api.skin.SubstanceNebulaAmethystLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceSentinelLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceNightShadeLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteChalkLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteGoldLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteSiennaLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteSunsetLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel",
                "org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel"
        };

        List<UIManager.LookAndFeelInfo> oldOnes = Arrays.asList(UIManager.getInstalledLookAndFeels());


        addLaf(oldOnes, "FlatLight", "com.formdev.flatlaf.FlatLightLaf");

        addLaf(oldOnes, "FlatDark", "com.formdev.flatlaf.FlatDarkLaf");

        /*

        for (String lafClass : lafs) {
            String simpleName = lafClass.substring(lafClass.lastIndexOf('.') + 1);
            int from = "Substance".length();
            int to = simpleName.indexOf("LookAndFeel");
            String name = simpleName.substring(from, to);

            addLaf(oldOnes, name, lafClass);
        }
        */

        setLookAndFeel(lookAndFeel);

        JFrame frame = new JFrame();

        JMenu menu = new JMenu("Look and Feel");
        JMenu substance = new JMenu("Substance");

        UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
            JMenuItem item = new JMenuItem(lookAndFeelInfo.getName());
            item.addActionListener(event -> {
                try {

                    // test(panelSupplier, lookAndFeelInfo.getName(), frameConsumer);

                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    updateLaf();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (lookAndFeelInfo.getClassName().startsWith("org.pushingpixels")) {
                if (substance.getItemCount() == 0) {
                    menu.add(substance);
                }
                substance.add(item);
            } else {
                menu.add(item);
                ;
            }

        }

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);


        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(panelSupplier.apply(frame));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.pack();
        frameConsumer.accept(frame);


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void addLaf(List<UIManager.LookAndFeelInfo> oldOnes, String darcula, String s) {
        if (!contains(oldOnes, darcula)) {
            UIManager.installLookAndFeel(darcula, s);
        }
    }

    private static boolean contains(List<UIManager.LookAndFeelInfo> oldOnes, String name) {
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : oldOnes) {
            if (lookAndFeelInfo.getName().equals(name)) {
                return true;
            }
        }
        return false;

    }

    private static void setLookAndFeel(String lookAndFeel) {
        try {
            UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
            for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeelInfos) {
                if (lookAndFeelInfo.getName().contains(lookAndFeel)) {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName().toString());
                    updateLaf();
                }
            }

            // UIManager.getLookAndFeelDefaults().put("Table.alternateRowColor",
            // Color.LIGHT_GRAY);

        } catch (Exception ex) {

        }
    }

    private static void updateLaf() {
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            if (w.isDisplayable() && (w
                    instanceof Frame ? !((Frame) w).isResizable() : w instanceof Dialog ?
                    !((Dialog) w).isResizable() : true)) w.pack();
        }
    }
}
