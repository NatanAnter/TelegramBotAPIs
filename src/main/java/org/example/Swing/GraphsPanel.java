package org.example.Swing;

import org.example.API.GraphAPI.DataSet;
import org.example.API.GraphAPI.GraphAPIClient;
import org.example.MainDataFlow.Bot;
import org.example.MainDataFlow.UserStatistics;
import org.example.Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class GraphsPanel extends JPanel implements ActionListener {
    public static final int[] BOUNDS = ControlPanel.GRAPHS_PANEL_BOUNDS;
    private final JButton hour;
    private final JButton day;
    private final JButton week;
    private final JButton month;
    private final JButton year;
    private final JButton allTime;
    private final JButton chartType;
    private final JButton chartAccumulateStatus;
    private final JLabel numOfNewUsersImage;
    private final JLabel numOfMessagesImage;
    private final JLabel numOfQueriesImage;
    private List<String> labels;
    private boolean toAccumulate;
    private boolean lineType;
    private DisplayTime chosenTime;
    private List<Integer> usersCount;
    private List<Integer> messagesCount;
    private Map<Bot.Query, List<Integer>> queriesCount;
    private String type;
    private int numOfPoints;

    public GraphsPanel(JPanel jPanel, Bot bot) {
        bot.setGraphsPanel(this);
        Utils.initPanel(BOUNDS[0], BOUNDS[1], BOUNDS[2], BOUNDS[3], "Graphs", this, jPanel);
        Utils.initButton(new int[]{0, 0, BOUNDS[2] / 3, ControlPanel.LABELS_HEIGHT}, "hour", ControlPanel.TEXT_FONT, this, this, this.hour = new JButton());
        Utils.initButton(new int[]{hour.getX() + hour.getWidth(), hour.getY(), hour.getWidth(), hour.getHeight()}, "day", ControlPanel.TEXT_FONT, this, this, this.day = new JButton());
        Utils.initButton(new int[]{day.getX() + day.getWidth(), day.getY(), hour.getWidth(), hour.getHeight()}, "week", ControlPanel.TEXT_FONT, this, this, this.week = new JButton());
        Utils.initButton(new int[]{hour.getX(), hour.getY() + hour.getHeight(), hour.getWidth(), hour.getHeight()}, "month", ControlPanel.TEXT_FONT, this, this, this.month = new JButton());
        Utils.initButton(new int[]{month.getX() + month.getWidth(), month.getY(), hour.getWidth(), hour.getHeight()}, "year", ControlPanel.TEXT_FONT, this, this, this.year = new JButton());
        Utils.initButton(new int[]{year.getX() + year.getWidth(), month.getY(), hour.getWidth(), hour.getHeight()}, "all time", ControlPanel.TEXT_FONT, this, this, this.allTime = new JButton());
        Utils.initButton(new int[]{hour.getX(), allTime.getY() + allTime.getHeight(), BOUNDS[2] / 2, 2 * hour.getHeight()}, "accumulate data", ControlPanel.TEXT_FONT, this, this, this.chartAccumulateStatus = new JButton());
        Utils.initButton(new int[]{chartAccumulateStatus.getX() + chartAccumulateStatus.getWidth(), chartAccumulateStatus.getY(), BOUNDS[2] - chartAccumulateStatus.getWidth(), chartAccumulateStatus.getHeight()}, "bar", ControlPanel.TEXT_FONT, this, this, this.chartType = new JButton());
        this.type = "line";
        this.toAccumulate = false;
        this.lineType = true;
        this.numOfPoints = 20;
        this.chosenTime = DisplayTime.hour;
        this.labels = makeLabels(20, LocalDateTime.now().minusHours(1), LocalDateTime.now(), 6, Window.FORMAT_HOUR);
        this.usersCount = new ArrayList<>();
        this.messagesCount = new ArrayList<>();
        this.queriesCount = new HashMap<>();
        Arrays.stream(Bot.Query.values()).forEach(query -> queriesCount.put(query, new ArrayList<>()));
        Utils.initJLabel(0, chartAccumulateStatus.getY() + chartAccumulateStatus.getHeight(), BOUNDS[2], (BOUNDS[3] - chartAccumulateStatus.getY() - chartAccumulateStatus.getHeight() - 100) / 3, new BufferedImage(400, 300, 3), this.numOfNewUsersImage = new JLabel(), this, 0, 0);
        Utils.initJLabel(0, numOfNewUsersImage.getY() + numOfNewUsersImage.getHeight(), BOUNDS[2], (BOUNDS[3] - chartAccumulateStatus.getY() - chartAccumulateStatus.getHeight() - 100) / 3, new BufferedImage(400, 300, 3), this.numOfMessagesImage = new JLabel(), this, 0, 0);
        Utils.initJLabel(0, numOfMessagesImage.getY() + numOfMessagesImage.getHeight(), BOUNDS[2], (BOUNDS[3] - chartAccumulateStatus.getY() - chartAccumulateStatus.getHeight() - 100) / 3, new BufferedImage(400, 300, 3), this.numOfQueriesImage = new JLabel(), this, 0, 0);
        this.paintIndefinitely();
    }


    private void paintIndefinitely() {
        new Thread(() -> {
            while (true) {
                repaint();
                Utils.mySleep(1000);
                updateAll();
            }
        }).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    public void updateNumOfUsersGraph() {
        DataSet dataSet = new DataSet(type, "new users", new Color(206, 122, 255), 2, false, this.usersCount, new Color(233, 150, 255), 0, toAccumulate);
        List<DataSet> dataSets = List.of(dataSet);
        BufferedImage bufferedImage = GraphAPIClient.getGraph(labels, dataSets, "new Users", numOfNewUsersImage);
        this.numOfNewUsersImage.setIcon(Utils.scaleImage(bufferedImage, numOfNewUsersImage.getWidth(), numOfNewUsersImage.getHeight()));
    }

    public void updateNumOfMessagesGraph() {
        DataSet dataSet = new DataSet(type, "messages", new Color(70, 200, 200), 2, false, this.messagesCount, new Color(93, 173, 173), 0, toAccumulate);
        List<DataSet> dataSets = List.of(dataSet);
        BufferedImage bufferedImage = GraphAPIClient.getGraph(labels, dataSets, "new Users", numOfMessagesImage);
        this.numOfMessagesImage.setIcon(Utils.scaleImage(bufferedImage, numOfMessagesImage.getWidth(), numOfMessagesImage.getHeight()));
    }

    public void updateNumOfQueriesGraph() {
        List<Bot.Query> queries = Bot.getActivatedQueries();
        DataSet dataSet0;
        DataSet dataSet1;
        DataSet dataSet2;
        List<DataSet> dataSets = new ArrayList<>();
        if (queries.size() > 0) {
            dataSet0 = new DataSet(type, queries.get(0).toString(), new Color(255, 0, 0), 2, false, this.queriesCount.get(queries.get(0)), new Color(255, 152, 152), 0, toAccumulate);
            dataSets.add(dataSet0);
        }
        if (queries.size() > 1) {
            dataSet1 = new DataSet(type, queries.get(1).toString(), new Color(0, 255, 0), 2, false, this.queriesCount.get(queries.get(1)), new Color(123, 224, 123), 0, toAccumulate);
            dataSets.add(dataSet1);
        }
        if (queries.size() > 2) {
            dataSet2 = new DataSet(type, queries.get(2).toString(), new Color(0, 0, 255), 2, false, this.queriesCount.get(queries.get(2)), new Color(129, 251, 255), 0, toAccumulate);
            dataSets.add(dataSet2);
        }
        BufferedImage bufferedImage = GraphAPIClient.getGraph(labels, dataSets, "queries", numOfMessagesImage);
        this.numOfQueriesImage.setIcon(Utils.scaleImage(bufferedImage, numOfMessagesImage.getWidth(), numOfMessagesImage.getHeight()));
    }

    public void updateCounters(LocalDateTime start, LocalDateTime end, int numOfPoints) {
        this.usersCount = UserStatistics.getNewUsersCountForGraph(start, end, numOfPoints);
        this.messagesCount = UserStatistics.getMessagesForGraph(start, end, numOfPoints);
        this.queriesCount = UserStatistics.getQueriesCountForGraph(start, end, numOfPoints);

    }

    public void updateAll() {
        switch (chosenTime) {
            case hour -> {
                updateCounters(LocalDateTime.now().minusHours(1), LocalDateTime.now(), numOfPoints);
            }
            case day -> {
                updateCounters(LocalDateTime.now().minusDays(1), LocalDateTime.now(), numOfPoints);
            }
            case week -> {
                updateCounters(LocalDateTime.now().minusWeeks(1), LocalDateTime.now(), numOfPoints);
            }
            case month -> {
                updateCounters(LocalDateTime.now().minusMonths(1), LocalDateTime.now(), numOfPoints);
            }
            case year -> {
                updateCounters(LocalDateTime.now().minusYears(1), LocalDateTime.now(), numOfPoints);
            }
            case allTime -> {
                updateCounters(UserStatistics.getSignUpDate(UserStatistics.getFirstUser()), LocalDateTime.now(), numOfPoints);
            }
        }
        updateNumOfUsersGraph();
        updateNumOfMessagesGraph();
        updateNumOfQueriesGraph();
    }

    private List<String> makeLabels(int points, LocalDateTime first, LocalDateTime end, int labels, DateTimeFormatter dateTimeFormatter) {
        List<String> list = new ArrayList<>();
        int duration = points / labels;
        int j = 0;
        for (int i = 0; i < points; i++) {
            if (i % duration != 0) {
                list.add("");
            } else {
                list.add(UserStatistics.divideDates(first, end, labels).get(j++).format(dateTimeFormatter));
            }
        }
        return list;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hour) {
            updateCounters(LocalDateTime.now().minusHours(1), LocalDateTime.now(), numOfPoints);
            chosenTime = DisplayTime.hour;
            this.numOfPoints = 20;
            this.labels = makeLabels(numOfPoints, LocalDateTime.now().minusHours(1), LocalDateTime.now(), 6, Window.FORMAT_HOUR);
        } else if (e.getSource() == day) {
            updateCounters(LocalDateTime.now().minusDays(1), LocalDateTime.now(), numOfPoints);
            chosenTime = DisplayTime.day;
            this.numOfPoints = 12;
            this.labels = makeLabels(numOfPoints, LocalDateTime.now().minusDays(1), LocalDateTime.now(), 6, Window.FORMAT_HOUR);
        } else if (e.getSource() == week) {
            updateCounters(LocalDateTime.now().minusWeeks(1), LocalDateTime.now(), numOfPoints);

            chosenTime = DisplayTime.week;
            this.numOfPoints = 7;
            this.labels = makeLabels(numOfPoints, LocalDateTime.now().minusWeeks(1), LocalDateTime.now(), 7, Window.FORMAT_WEEK);
        } else if (e.getSource() == month) {
            updateCounters(LocalDateTime.now().minusMonths(1), LocalDateTime.now(), numOfPoints);

            chosenTime = DisplayTime.month;
            this.numOfPoints = 20;
            this.labels = makeLabels(numOfPoints, LocalDateTime.now().minusMonths(1), LocalDateTime.now(), 10, Window.FORMAT_DATE);
        } else if (e.getSource() == year) {
            chosenTime = DisplayTime.year;
            updateCounters(LocalDateTime.now().minusYears(1), LocalDateTime.now(), numOfPoints);
            this.numOfPoints = 12;
            this.labels = makeLabels(numOfPoints, LocalDateTime.now().minusYears(1), LocalDateTime.now(), 6, Window.FORMAT_YEAR);
        } else if (e.getSource() == allTime) {
            updateCounters(UserStatistics.getSignUpDate(UserStatistics.getFirstUser()), LocalDateTime.now(), numOfPoints);
            chosenTime = DisplayTime.allTime;
            this.numOfPoints = 100;
            this.labels = makeLabels(numOfPoints, LocalDateTime.now().minusYears(1), LocalDateTime.now(), 10, Window.FORMAT_DATE_TIME);
        } else if (e.getSource() == chartAccumulateStatus) {
            this.toAccumulate = !toAccumulate;
            if (toAccumulate) {
                this.chartAccumulateStatus.setText("deplete data");
            } else {
                this.chartAccumulateStatus.setText("accumulate data");
            }
        } else if (e.getSource() == chartType) {
            this.lineType = !lineType;
            if (lineType) {
                this.chartType.setText("bar");
                this.type = "line";
            } else {
                this.chartType.setText("line");
                this.type = "bar";
            }
        }
        updateAll();
    }

    enum DisplayTime {
        hour, day, week, month, year, allTime
    }
}
