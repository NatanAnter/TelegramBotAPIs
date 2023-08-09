package org.example.Swing;

import org.example.MainDataFlow.Activity;
import org.example.MainDataFlow.UserStatistics;
import org.example.Utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

public class LastActivitiesPanel extends JPanel {
    public static final int[] BOUNDS = ControlPanel.LAST_ACTIVITIES_BOUNDS;
    public static final int NUMBER_OF_STATISTICS = 10;
    private JTable jTable;
    private DefaultTableModel tableModel;

    public LastActivitiesPanel(JPanel jPanel) {

        Utils.initPanel(BOUNDS[0], BOUNDS[1], BOUNDS[2], BOUNDS[3], "Last activities", this, jPanel);
        Object[][] data = IntStream.range(0, NUMBER_OF_STATISTICS).mapToObj(index -> new Object[]{index + 1 + ": ", "", ""}).toArray(Object[][]::new);
        tableModel = new DefaultTableModel(data, new String[]{"", "", ""});
        jTable = new JTable(tableModel);
        jTable.setBackground(Color.LIGHT_GRAY);
        jTable.setBounds(ControlPanel.SPACE, ControlPanel.SPACE, BOUNDS[2] - 2 * ControlPanel.SPACE, BOUNDS[3] - 2 * ControlPanel.SPACE);
        jTable.setTableHeader(null);
        this.add(jTable);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                cellComponent.setForeground(Color.RED);
                cellComponent.setFont(new Font("Arial", Font.PLAIN, 22));
                cellComponent.setBackground(new Color(0, 0, 0, 0));
                setBorder(BorderFactory.createLineBorder(Color.BLUE, 0));

                return cellComponent;
            }
        };
        jTable.setShowGrid(false);
        jTable.setSelectionBackground(new Color(0, 0, 0, 0));
        jTable.setDefaultRenderer(Object.class, cellRenderer);
        int customRowHeight = 60;
        jTable.setRowHeight(customRowHeight);
        this.paintIndefinitely();

    }

    private void paintIndefinitely() {
        new Thread(() -> {
            while (true) {
                repaint();
                Utils.mySleep(100);
            }
        }).start();
    }

    private void changeRowText(int row, String number, List<String> texts) {
        tableModel.setValueAt(number + texts.get(0), row, 0);
        tableModel.setValueAt(texts.get(1), row, 1);
        tableModel.setValueAt(texts.get(2), row, 2);
    }

    private void changeCellText(int row, int column, String newText) {
        tableModel.setValueAt(newText, row, column);
    }

    @Override
    protected void paintComponent(Graphics g) {
        List<Activity> activityList = UserStatistics.getActivitiesHistory().stream().filter(Activity::isQueryType).toList();
        super.paintComponent(g);
        for (int i = 0; i < NUMBER_OF_STATISTICS; i++)
            if (0 <= activityList.size() - i - 1) {
                this.changeRowText(i, (i + 1) + ": ", activityList.get(activityList.size() - i - 1).toListString());
            }

    }
}
