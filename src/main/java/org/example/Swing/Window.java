package org.example.Swing;

import org.example.MainDataFlow.Bot;
import org.example.MainDataFlow.UserStatistics;
import org.example.Utils.Utils;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;


public class Window extends JFrame {
    public static final DateTimeFormatter FORMAT_DATE_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public static final DateTimeFormatter FORMAT_YEAR = DateTimeFormatter.ofPattern("MMMM");
    public static final DateTimeFormatter FORMAT_WEEK = DateTimeFormatter.ofPattern("EEEE");
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd-MM");
    public static final DateTimeFormatter FORMAT_HOUR = DateTimeFormatter.ofPattern("HH:mm");
    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;
    private Bot bot;


    public Window() {
        initAll();
        this.setVisible(false);
        this.setResizable(true);
        this.setExtendedState(/*this.getExtendedState() |*/ JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() == JFrame.NORMAL) {
                    setSize(800, 600);
                }
            }
        });
        this.setLocationRelativeTo(null);
        new ControlPanel(this, bot);
        Utils.mySleep(10);
        this.setVisible(true);
    }

    public void initAll() {
        createBot();
        initStatistic();

    }

    public void initStatistic() {
        new UserStatistics();
    }

    public void createBot() {
        this.bot = new Bot();
//        initBot();
    }

    public void initBot() {

        try {
            Bot.setInternetProblem(!Utils.isInternetAvailable());
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            Bot.setBotFound(true);
            Bot.setRunning(true);

        } catch (TelegramApiException e) {
            Bot.setBotFound(false);
            Bot.setRunning(false);
        }
    }

    public void showWindow() {
        this.setVisible(true);
    }


}
