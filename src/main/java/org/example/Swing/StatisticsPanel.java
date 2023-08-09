package org.example.Swing;

import org.example.MainDataFlow.UserStatistics;
import org.example.Utils.Utils;

import javax.swing.*;
import java.awt.*;

public class StatisticsPanel extends JPanel {
    public static final int[] BOUNDS = ControlPanel.STATISTICS_PANEL_BOUNDS;
    public static final int LABEL_WIDTH = 600;
    private final JLabel numOfMessages;
    private final JLabel numOfQueries;
    private final JLabel numOfUsers;
    private final JLabel mostPopularUser;
    private final JLabel mostPopularQuery;

    public StatisticsPanel(JPanel jPanel) {
        Utils.initPanel(BOUNDS[0], BOUNDS[1], BOUNDS[2], BOUNDS[3], "Statistics", this, jPanel);
        int i = 0;
        Utils.initJLabel(ControlPanel.SPACE, ControlPanel.LABELS_HEIGHT * i + ++i * ControlPanel.SPACE, LABEL_WIDTH, ControlPanel.LABELS_HEIGHT, "Number of Messages: ", this.numOfMessages = new JLabel(), this, SwingConstants.LEFT, SwingConstants.CENTER);
        Utils.initJLabel(ControlPanel.SPACE, ControlPanel.LABELS_HEIGHT * i + ++i * ControlPanel.SPACE, LABEL_WIDTH, ControlPanel.LABELS_HEIGHT, "Number of Queries: ", this.numOfQueries = new JLabel(), this, SwingConstants.LEFT, SwingConstants.CENTER);
        Utils.initJLabel(ControlPanel.SPACE, ControlPanel.LABELS_HEIGHT * i + ++i * ControlPanel.SPACE, LABEL_WIDTH, ControlPanel.LABELS_HEIGHT, "Number of Users: ", this.numOfUsers = new JLabel(), this, SwingConstants.LEFT, SwingConstants.CENTER);
        Utils.initJLabel(ControlPanel.SPACE, ControlPanel.LABELS_HEIGHT * i + ++i * ControlPanel.SPACE, LABEL_WIDTH, ControlPanel.LABELS_HEIGHT, "most popular User: ", this.mostPopularUser = new JLabel(), this, SwingConstants.LEFT, SwingConstants.CENTER);
        Utils.initJLabel(ControlPanel.SPACE, ControlPanel.LABELS_HEIGHT * i + ++i * ControlPanel.SPACE, LABEL_WIDTH, ControlPanel.LABELS_HEIGHT, "most popular Query: ", this.mostPopularQuery = new JLabel(), this, SwingConstants.LEFT, SwingConstants.CENTER);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.numOfMessages.setText("Number of Messages: " + UserStatistics.getNumOfMessages());
        this.numOfQueries.setText("Number of Queries: " + UserStatistics.getNumOfQueries());
        this.numOfUsers.setText("Number of Users: " + UserStatistics.getNumOfUsers());
        this.mostPopularUser.setText("most popular User: " + UserStatistics.getNameORId(UserStatistics.getMostActiveUser()) + " (" + UserStatistics.getNumberOfMessages(UserStatistics.getMostActiveUser()) + ")");
        this.mostPopularQuery.setText("most popular Query: " + (UserStatistics.getMostPopularQuery() == null ? "" : UserStatistics.getMostPopularQuery()));
    }
}
