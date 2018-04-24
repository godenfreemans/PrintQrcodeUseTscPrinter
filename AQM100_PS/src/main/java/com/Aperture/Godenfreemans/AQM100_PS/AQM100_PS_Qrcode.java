package com.Aperture.Godenfreemans.AQM100_PS;

import com.Aperture.TSPL.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

public class AQM100_PS_Qrcode {

    //region JUI
    private JTextField yearTextField;
    private JTextField weekTextField;
    private JTextField snTextField;
    private JTextField macTextField;
    private JTextArea stateTextArea1;
    private JButton reprintButton;
    private JPanel Root;
    private JTable mTable;
    private JLabel JLabel1;
    private JLabel JLabel4;
    private JLabel JLabel5;
    private JPanel JPanel1;
    private JPanel JPanel1_1;
    private JPanel JPanel2;
    private JScrollPane JScrollpane1;
    private JScrollPane JScrollpane2;
    private JLabel JLabel2_1;
    private JLabel JLabel2_2;
    //endregion

    //region Field
    private static File logFile;
    private static ArrayList<File> logFileArrayList;
    private static ArrayList<String> MAC_Records = new ArrayList<>();
    private static ArrayList<String> SN_Records = new ArrayList<>();
    private static Integer Number = 1;
    private static String todayRecordName = new SimpleDateFormat("yyyyww").format(new Date()); // 201802
    //    private static String todayRecordName = "201810";
    private final Object[] columnNames = {
            "MAC"
    };
    //endregion

    private AQM100_PS_Qrcode() {
        // set UI
        yearTextField.setText(todayRecordName.substring(0, 4));
        weekTextField.setText(todayRecordName.substring(4));
        reprintButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    PrintLabel(snTextField.getText(), MAC_Records.get(SN_Records.indexOf(snTextField.getText())));
                } catch (ArrayIndexOutOfBoundsException e1) {
                    stateTextArea1.append("No data!" + '\n');
                }
            }
        });

        mTable.setModel(new AbstractTableModel() {
            public String getColumnName(int column) {
                return columnNames[column].toString();
            }

            public int getRowCount() {
                return MAC_Records.size();
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int row, int col) {
                return MAC_Records.get(row).split(",")[2];
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

        // check fold
        File macListFold = new File("macListFold");
        if (!macListFold.exists()) {    // Fold not exist
            stateTextArea1.append("AQM100_PS_Qrcode: 文件夹不存在啊。\n");
            if (macListFold.mkdir()) {
                stateTextArea1.append("AQM100_PS_Qrcode: 创建文件夹成功！\n");
            } else {
                stateTextArea1.append("AQM100_PS_Qrcode: 创建文件夹失败。。。\n");
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
                        stateTextArea1.append("AQM100_PS_Qrcode: 创建记录文件成功！\n");
                    }
                }
            } catch (IOException e1) {
                stateTextArea1.append("AQM100_PS_Qrcode: 创建记录文件失败！\n");
            }
        }

        // read every line
        try {
            for (File file : logFileArrayList) {
                int lineNumber = 1;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String tempLine;
                while ((tempLine = bufferedReader.readLine()) != null) {
                    String mac = tempLine.split(",", 2)[1];
                    if (!MAC_Records.contains(mac)) {
                        MAC_Records.add(mac);
                    } else {
                        stateTextArea1
                                .append("AQM100_PS_Qrcode: 记录在: " + file
                                        .toString() + " 文件中的第 " + lineNumber + " 行的 MAC 出现错误！\n");
                    }

                    String sn = tempLine.split(",", 2)[0];
                    if (!SN_Records.contains(sn)) {
                        SN_Records.add(sn);
                    } else {
                        stateTextArea1
                                .append("AQM100_PS_Qrcode: 记录在: " + file
                                        .toString() + " 文件中的第 " + lineNumber + " 行的  SN 出现错误！\n");
                    }
                    if (file.getName().equals(todayRecordName + ".csv")) {
                        Number++;
                    }
                    lineNumber++;
                }
                bufferedReader.close();
                if (MAC_Records.size() == SN_Records.size()) {
                    stateTextArea1.append("AQM100_PS_Qrcode: 读取到 " + MAC_Records.size() + " 个记录。\n");
                }
            }
        } catch (IOException e) {
            stateTextArea1.append("AQM100_PS_Qrcode: " + "读取记录失败！\n");
        }

        new Timer().schedule(new UdpReceive(), 1000, 1000);
    }

    public static void main() {
        JFrame frame = new JFrame("AQM100_PS_Qrcode");
        frame.setContentPane(new AQM100_PS_Qrcode().Root);
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
        Root.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        JLabel1 = new JLabel();
        this.$$$loadLabelText$$$(JLabel1, ResourceBundle.getBundle("AQM100_PS_Qrcode").getString("JLabel1"));
        Root.add(JLabel1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JPanel1 = new JPanel();
        JPanel1.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        Root.add(JPanel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(250, -1), new Dimension(700, 234), null, 0, false));
        JPanel1_1 = new JPanel();
        JPanel1_1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        JPanel1.add(JPanel1_1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        JLabel2_1 = new JLabel();
        this.$$$loadLabelText$$$(JLabel2_1, ResourceBundle.getBundle("AQM100_PS_Qrcode").getString("JLabel2"));
        JPanel1_1.add(JLabel2_1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yearTextField = new JTextField();
        yearTextField.setEditable(true);
        yearTextField.setText("");
        JPanel1_1.add(yearTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 25), null, 0, false));
        JLabel2_2 = new JLabel();
        this.$$$loadLabelText$$$(JLabel2_2, ResourceBundle.getBundle("AQM100_PS_Qrcode").getString("JLabel3"));
        JPanel1_1.add(JLabel2_2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        weekTextField = new JTextField();
        weekTextField.setEditable(true);
        JPanel1_1.add(weekTextField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        snTextField = new JTextField();
        snTextField.setBackground(new Color(-14632183));
        Font snTextFieldFont = this.$$$getFont$$$(null, -1, 28, snTextField.getFont());
        if (snTextFieldFont != null) snTextField.setFont(snTextFieldFont);
        snTextField.setText("");
        JPanel1.add(snTextField, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), null, 0, false));
        JLabel4 = new JLabel();
        Font JLabel4Font = this.$$$getFont$$$(null, -1, 36, JLabel4.getFont());
        if (JLabel4Font != null) JLabel4.setFont(JLabel4Font);
        JLabel4.setForeground(new Color(-14632183));
        this.$$$loadLabelText$$$(JLabel4, ResourceBundle.getBundle("AQM100_PS_Qrcode").getString("JLabel4"));
        JPanel1.add(JLabel4, new GridConstraints(2, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JLabel5 = new JLabel();
        Font JLabel5Font = this.$$$getFont$$$(null, -1, 36, JLabel5.getFont());
        if (JLabel5Font != null) JLabel5.setFont(JLabel5Font);
        JLabel5.setForeground(new Color(-14632183));
        this.$$$loadLabelText$$$(JLabel5, ResourceBundle.getBundle("AQM100_PS_Qrcode").getString("JLabel5"));
        JPanel1.add(JLabel5, new GridConstraints(2, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), null, null, 0, false));
        macTextField = new JTextField();
        macTextField.setBackground(new Color(-14632183));
        macTextField.setEditable(true);
        macTextField.setEnabled(true);
        Font macTextFieldFont = this.$$$getFont$$$(null, -1, 28, macTextField.getFont());
        if (macTextFieldFont != null) macTextField.setFont(macTextFieldFont);
        macTextField.setText("");
        JPanel1.add(macTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        JPanel1.add(spacer1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        JPanel2 = new JPanel();
        JPanel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        Root.add(JPanel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(82, 123), null, 0, false));
        reprintButton = new JButton();
        this.$$$loadButtonText$$$(reprintButton, ResourceBundle.getBundle("AQM100_PS_Qrcode").getString("reprintButton"));
        JPanel2.add(reprintButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        JPanel2.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        JScrollpane1 = new JScrollPane();
        Root.add(JScrollpane1, new GridConstraints(1, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mTable = new JTable();
        JScrollpane1.setViewportView(mTable);
        JScrollpane2 = new JScrollPane();
        Root.add(JScrollpane2, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stateTextArea1 = new JTextArea();
        JScrollpane2.setViewportView(stateTextArea1);
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
    private void $$$loadLabelText$$$(JLabel component, String text) {
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
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
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

    class UdpReceive extends TimerTask {

        @Override
        public void run() {
            DatagramSocket socket;
            try {
                socket = new DatagramSocket(8080);
            } catch (SocketException e) {
                stateTextArea1.append("\tUdpReceive: 8080 已被占用!" + '\n');
                stateTextArea1.append(e.getMessage() + '\n');
                return;
            }

            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                stateTextArea1.append(e.getMessage() + '\n');
                return;
            }
            byte[] packetData = packet.getData();
            String data = new String(packetData, 0, packet.getLength()).replaceAll("\r\n", "");
            stateTextArea1.append("\tUdpReceive: 获得数据: " + '\"' + data + '\"' + '\n');
            macTextField.setText(data.split(",")[2]);

            socket.close();

            if (MAC_Records.contains(data)) {
                snTextField.setText(SN_Records.get(MAC_Records.indexOf(data)));
                stateTextArea1.append("\tUdpReceive: " + data + "已存在于记录中；SN :" +
                        SN_Records.get(MAC_Records.indexOf(data)) + '\n');
            } else {
                snTextField.setText("000" + todayRecordName + String.valueOf(100000 + Number)
                        .substring(1));
                Number++;
                MAC_Records.add(data);
                SN_Records.add(snTextField.getText());
                if (SaveNewData()) {
                    stateTextArea1.append("\tUdpReceive: 保存记录成功." + '\n');
                }
//                    PrintLabel("000" + todayRecordName + String.valueOf(100000 + MAC_Records.size())
//                                                               .substring(1), data);
                PrintLabel(SN_Records.get(MAC_Records.indexOf(data)), data);
                mTable.updateUI();
            }
        }
    }

    private void PrintLabel(String snData, String macData) {
        stateTextArea1.append("\t\tPrintLabel: 打印 QRCODE " + macData + '\n');

        TscLibDll.INSTANCE.openport("TSC TTP-342M Pro");
        TscLibDll.INSTANCE.clearbuffer();
        TscLibDll.INSTANCE.setup("83.4", "29", "2", "8",
                "0", "2.54", "0");
        TscLibDll.INSTANCE.sendcommand(new DIRECTION(1).getCOMMAND());
        TscLibDll.INSTANCE.sendcommand(new SHIFT(10, -20).getCOMMAND());
        TscLibDll.INSTANCE.sendcommand(new QRCODE(690, 40, 'L', 10, 'A',
                0, "M2", "S7", macData).getCOMMAND());
        TscLibDll.INSTANCE.sendcommand(new BARCODE(70, 135, "128", 50, 0,
                0, 3, 7, 0, snData).getCOMMAND());
        TscLibDll.INSTANCE.sendcommand(new TEXT(90, 185, "3", 0, 1,
                1, 0, "SN: " + snData).getCOMMAND());
        TscLibDll.INSTANCE.printlabel("1", "1");
        TscLibDll.INSTANCE.closeport();
    }

    private boolean SaveNewData() {
        // write SN and MAC
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(SN_Records.get(SN_Records.size() - 1))
                    .append(String.valueOf(','))
                    .append(MAC_Records.get(MAC_Records.size() - 1));
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        stateTextArea1.append("\t\tSaveNewData: " + SN_Records.get(SN_Records.size() - 1) + ',' + MAC_Records
                .get(MAC_Records.size() - 1) + '\n');
        return true;
    }


}
