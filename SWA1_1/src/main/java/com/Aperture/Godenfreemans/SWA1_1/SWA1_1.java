package com.Aperture.Godenfreemans.SWA1_1;

import com.Aperture.TSPL.DIRECTION;
import com.Aperture.TSPL.QRCODE;
import com.Aperture.TSPL.TscLibDll;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class SWA1_1 {

    private JPanel Root;
    private JTable mFileTabel;
    private JTable mDataTabel;
    private JTextArea stateTextArea;
    private JButton printThisFileButton;


    public SWA1_1() {

        // check fold
        File csvFileFold = new File("csvFileFold");
        if (!csvFileFold.exists()) {    // Fold not exist
            stateTextArea.append("-MSG- Fold does not exist.\n");
            if (csvFileFold.mkdir()) {
                stateTextArea.append("-MSG- Creat fold successful！\n");
            } else {
                stateTextArea.append("-ERR- Creat fold fail！\n");
            }
        } else {
            stateTextArea.append("-MSG- Fold exist.\n");
        }

        // read file list
        ArrayList<File> csvFilesArrayList;
        ArrayList<String> csvData = new ArrayList<>();
        csvFilesArrayList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(csvFileFold.listFiles())));
        if (csvFilesArrayList.isEmpty()) {
            stateTextArea.append("-MSG- No *.csv file.\n");
        } else {
            stateTextArea.append("-MSG- Find " + csvFilesArrayList.size() + " files.\n");
        }

        mFileTabel.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return csvFilesArrayList.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public String getColumnName(int column) {
                return "*.csv file";
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return csvFilesArrayList.get(rowIndex).getName();
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

        mFileTabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                csvData.clear();
                File csvFile = csvFilesArrayList.get(mFileTabel.getSelectedRow());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));
                    String tempLine;
                    while ((tempLine = bufferedReader.readLine()) != null) {
                        if (!(csvData.contains(tempLine)) || (tempLine.contains(" "))) {
                            csvData.add(tempLine);
                        } else {
                            stateTextArea.append("-ERR- another " + csvData.indexOf(tempLine) +
                                    " at file: " + csvFile.toString() + "！\n");
                        }
                    }
                    stateTextArea.append("-MSG- Read " + csvData.size() + " data." + "\n");
                    bufferedReader.close();
                } catch (FileNotFoundException e) {
                    stateTextArea.append("-ERR- File not found!");
                } catch (IOException e) {
                    stateTextArea.append("-ERR- File read error!");
                }
                mDataTabel.updateUI();
            }
        });

        mDataTabel.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return csvData.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return csvData.get(rowIndex);
            }

            @Override
            public void fireTableDataChanged() {
            }
        });

        printThisFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                new Thread(() -> PrintLabel(csvData)).run();
            }
        });
    }

    private void PrintLabel(ArrayList<String> strings) {
        TscLibDll.INSTANCE.openport("TSC TTP-342M Pro");
        TscLibDll.INSTANCE.setup("72", "25", "2", "7",
                "0", "3", "0");
        TscLibDll.INSTANCE.sendcommand(new DIRECTION(1).getCOMMAND());

        for (int i = 0; i < strings.size(); i += 2) {
            TscLibDll.INSTANCE.sendcommand(new QRCODE(670, 55, 'M', 7, 'A',
                    0, "M2", "S7", strings.get(i)).getCOMMAND());
            stateTextArea.append("-MSG- Print " + strings.get(i) + "\n");
            if ((i + 1) < strings.size()) {
                TscLibDll.INSTANCE.sendcommand(new QRCODE(230, 55, 'M', 7, 'A',
                        0, "M2", "S7", strings.get(i + 1)).getCOMMAND());
                stateTextArea.append("-MSG- Print " + strings.get(i + 1) + "\n");
            }
            TscLibDll.INSTANCE.printlabel("1", "1");
            TscLibDll.INSTANCE.clearbuffer();
        }

        TscLibDll.INSTANCE.closeport();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SWA1_1");
        frame.setContentPane(new SWA1_1().Root);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        Root = new JPanel();
        Root.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        Root.setBorder(BorderFactory.createTitledBorder(null, ResourceBundle.getBundle("SWA1_1")
                                                                            .getString("JLabel"), TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        final JScrollPane scrollPane1 = new JScrollPane();
        Root.add(scrollPane1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 100), null, 0, false));
        stateTextArea = new JTextArea();
        stateTextArea.setText("");
        scrollPane1.setViewportView(stateTextArea);
        final JScrollPane scrollPane2 = new JScrollPane();
        Root.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 300), null, 0, false));
        mFileTabel = new JTable();
        scrollPane2.setViewportView(mFileTabel);
        final JScrollPane scrollPane3 = new JScrollPane();
        Root.add(scrollPane3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 300), null, 0, false));
        mDataTabel = new JTable();
        scrollPane3.setViewportView(mDataTabel);
        printThisFileButton = new JButton();
        this.$$$loadButtonText$$$(printThisFileButton, ResourceBundle.getBundle("SWA1_1").getString("jButton"));
        Root.add(printThisFileButton, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Root;
    }
}
