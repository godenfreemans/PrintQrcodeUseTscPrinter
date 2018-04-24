package com.Aperture.Godenfreemans.AQM100_PS;

import com.Aperture.TSPL.BARCODE;
import com.Aperture.TSPL.DIRECTION;
import com.Aperture.TSPL.TEXT;
import com.Aperture.TSPL.TscLibDll;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class AQM100_PS_Sncode {
    private JPanel Root;
    private JTextArea stateTextArea;
    private JTextField yearTextField;
    private JTextField weekTextField;
    private JTextField snTextField;
    private JTextField macTextField;
    private JButton mALLButton;
    private JButton mThisButton;
    private JTable mLogListList;
    private JTable mTable;

    private static File logFile;
    private static ArrayList<File> logFileArrayList;
    private static ArrayList<String> MAC_Records = new ArrayList<>();
    private static ArrayList<String> SN_Records = new ArrayList<>();
    private static String todayRecordName = new SimpleDateFormat("yyyyww").format(new Date()); // 201802
    private final Object[] columnNames = {
            "SN",
            "MAC"
    };

    private AQM100_PS_Sncode() {
        // set UI
        yearTextField.setText(todayRecordName.substring(0, 4));
        weekTextField.setText(todayRecordName.substring(4));


        // check fold
        File macListFold = new File("macListFold");
        if (!macListFold.exists()) {    // Fold not exist
            stateTextArea.append("AQM100_PS_Qrcode: 文件夹不存在啊。\n");
            if (macListFold.mkdir()) {
                stateTextArea.append("AQM100_PS_Qrcode: 创建文件夹成功！\n");
            } else {
                stateTextArea.append("AQM100_PS_Qrcode: 创建文件夹失败。。。\n");
            }
        }

        // check logFileArrayList
        logFile = new File(macListFold, todayRecordName + ".csv");
        try {
            logFileArrayList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(macListFold.listFiles())));
        } catch (NullPointerException e) {
            logFileArrayList.add(logFile);
            try {
                if (!logFile.exists()) {
                    if (logFile.createNewFile()) {
                        stateTextArea.append("AQM100_PS_Qrcode: 创建记录文件成功！\n");
                    }
                }
            } catch (IOException e1) {
                stateTextArea.append("AQM100_PS_Qrcode: 创建记录文件失败！\n");
            }
        }

        // list log file
        mLogListList.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return logFileArrayList.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return logFileArrayList.get(rowIndex);
            }
        });

        // read select log file
        mLogListList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logFile = logFileArrayList.get(mLogListList.getSelectedRow());
                mTable.removeAll();
                MAC_Records.clear();
                SN_Records.clear();
                new Thread(() -> {
                    // read every line
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
                        String tempLine;
                        while ((tempLine = bufferedReader.readLine()) != null) {
                            String mac = tempLine.split(",", 2)[1];
                            if (!MAC_Records.contains(mac)) {
                                MAC_Records.add(mac);
                            } else {
                                stateTextArea
                                        .append("AQM100_PS_Qrcode: 记录在:" + (MAC_Records.size() + 1) + "出现错误！\n");
                            }

                            String sn = tempLine.split(",", 2)[0];
                            if (!SN_Records.contains(sn)) {
                                SN_Records.add(sn);
                            }
                        }
                        bufferedReader.close();
                        if (MAC_Records.size() == SN_Records.size()) {
                            stateTextArea.append("AQM100_PS_Qrcode: 读取到 " + MAC_Records.size() + " 个记录。.\n");
                        }
                    } catch (IOException e1) {
                        stateTextArea.append(e1.getMessage() + '\n');
                    }
                    mTable.updateUI();
                }).run();
            }
        });

        // fill data
        mTable.setModel(new AbstractTableModel() {
            public String getColumnName(int column) {
                return columnNames[column].toString();
            }

            public int getRowCount() {
                return SN_Records.size();
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int row, int col) {
                return col == 0 ? SN_Records.get(row) : MAC_Records.get(row);
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        mTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                snTextField.setText(SN_Records.get(mTable.getSelectedRow()));
                macTextField.setText(MAC_Records.get(mTable.getSelectedRow()).split(",")[2]);
            }
        });


        // setup button
        mALLButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (String sn : SN_Records) {
                    PrintLabel(sn);
                }
            }
        });
        mThisButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PrintLabel(snTextField.getText());
            }
        });


        new Thread(this::UdpReceive).start();

    }

    private void PrintLabel(String snData) {
        stateTextArea.append("\t\tPrintLabel: 打印 SN： \"" + snData + '\n');
        TscLibDll.INSTANCE.openport("TSC TTP-342M Pro");
        TscLibDll.INSTANCE.clearbuffer();
        TscLibDll.INSTANCE.setup("70", "15", "2", "8", "0", "2", "0");
        TscLibDll.INSTANCE.sendcommand(new DIRECTION(1).getCOMMAND());
        TscLibDll.INSTANCE
                .sendcommand(new BARCODE(180, 25, "128", 80, 0,
                        0, 4, 10, 0, snData).getCOMMAND());
        TscLibDll.INSTANCE
                .sendcommand(new TEXT(185, 105, "4", 0, 1,
                        1, 0, "SN: " + snData).getCOMMAND());
        TscLibDll.INSTANCE.printlabel("1", "1");
        TscLibDll.INSTANCE.closeport();
    }

    private void UdpReceive() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(8080);
        } catch (SocketException e) {
            stateTextArea.append("\tUdpReceive: 8080 已被占用!" + '\n');
            stateTextArea.append(e.getMessage() + '\n');
            return;
        }

        for (int i = 0; i < 10; i++) {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                stateTextArea.append(e.getMessage() + '\n');
                return;
            }
            byte[] packetData = packet.getData();
            String data = new String(packetData, 0, packet.getLength()).replaceAll("\r\n", "");
            stateTextArea.append("\tUdpReceive: 获得数据: " + '\"' + data + '\"' + '\n');
            macTextField.setText(data.split(",")[2]);
            if (MAC_Records.contains(data)) {
                snTextField.setText(SN_Records.get(MAC_Records.indexOf(data)));
                stateTextArea.append("\tUdpReceive: 这个产品已经记录过了" + '\n');
            } else {
                snTextField.setText("");
                stateTextArea.append("\tUdpReceive: 这个产品没有记录过" + '\n');
            }
        }
    }

    public static void main() {
        JFrame frame = new JFrame("AQM100_PS_Sncode");
        frame.setContentPane(new AQM100_PS_Sncode().Root);
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
        Root.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        Root.add(panel1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("year");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yearTextField = new JTextField();
        yearTextField.setEditable(false);
        yearTextField.setText("");
        panel1.add(yearTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 25), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("week");
        panel1.add(label2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        weekTextField = new JTextField();
        weekTextField.setEditable(false);
        panel1.add(weekTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("AQM100-PS");
        Root.add(label3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        Root.add(panel2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(250, -1), new Dimension(500, -1), null, 0, false));
        snTextField = new JTextField();
        snTextField.setBackground(new Color(-14632183));
        Font snTextFieldFont = this.$$$getFont$$$(null, -1, 28, snTextField.getFont());
        if (snTextFieldFont != null) snTextField.setFont(snTextFieldFont);
        snTextField.setText("00000000000000");
        panel2.add(snTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, -1, 36, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setForeground(new Color(-14632183));
        label4.setText("SN");
        panel2.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, -1, 36, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("MAC");
        panel2.add(label5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), null, null, 0, false));
        macTextField = new JTextField();
        Font macTextFieldFont = this.$$$getFont$$$(null, -1, 28, macTextField.getFont());
        if (macTextFieldFont != null) macTextField.setFont(macTextFieldFont);
        macTextField.setText("00:00:00:00:00:00");
        panel2.add(macTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), null, 0, false));
        mALLButton = new JButton();
        mALLButton.setText("打印全部条码");
        Root.add(mALLButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mThisButton = new JButton();
        mThisButton.setText("打印这个条码");
        Root.add(mThisButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(217, 33), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        Root.add(panel3, new GridConstraints(0, 3, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mTable = new JTable();
        scrollPane1.setViewportView(mTable);
        final JScrollPane scrollPane2 = new JScrollPane();
        Root.add(scrollPane2, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stateTextArea = new JTextArea();
        scrollPane2.setViewportView(stateTextArea);
        final JScrollPane scrollPane3 = new JScrollPane();
        Root.add(scrollPane3, new GridConstraints(0, 2, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mLogListList = new JTable();
        scrollPane3.setViewportView(mLogListList);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Root;
    }
}
