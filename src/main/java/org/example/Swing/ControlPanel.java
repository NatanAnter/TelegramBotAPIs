package org.example.Swing;

import org.example.MainDataFlow.Bot;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    public static final int SLEEP_TIME = 50;
    public static final int SPACE = 15;
    public static final int[] CREATE_BOT_PANEL_BOUNDS = {0, 0, 500, 400};
    public static final int[] STATISTICS_PANEL_BOUNDS = {CREATE_BOT_PANEL_BOUNDS[2], CREATE_BOT_PANEL_BOUNDS[1], 600, CREATE_BOT_PANEL_BOUNDS[3]};
    public static final int[] LAST_ACTIVITIES_BOUNDS = {CREATE_BOT_PANEL_BOUNDS[0], CREATE_BOT_PANEL_BOUNDS[1] + CREATE_BOT_PANEL_BOUNDS[3], CREATE_BOT_PANEL_BOUNDS[2] + STATISTICS_PANEL_BOUNDS[2], Window.WINDOW_HEIGHT - (CREATE_BOT_PANEL_BOUNDS[1] + CREATE_BOT_PANEL_BOUNDS[3] + SPACE)};
    public static final int[] GRAPHS_PANEL_BOUNDS = {CREATE_BOT_PANEL_BOUNDS[2] + STATISTICS_PANEL_BOUNDS[2], CREATE_BOT_PANEL_BOUNDS[1], Window.WINDOW_WIDTH - (CREATE_BOT_PANEL_BOUNDS[2] + STATISTICS_PANEL_BOUNDS[2]), Window.WINDOW_HEIGHT};
    public static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 24);
    public static final int LABELS_HEIGHT = 40;
    private final Window window;

    public ControlPanel(Window jFramewindow, Bot bot) {
        this.window = jFramewindow;
        this.setBounds(0, 0, Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        this.setLayout(null);
        jFramewindow.add(this);
        new LastActivitiesPanel(this);
        new StatisticsPanel(this);
        new CreateBotPanel(this);
        new GraphsPanel(this, bot);
//        paintIndefinitely();

    }

    //    public void paintIndefinitely(){
//        new Thread(()->{
//            while(true){
//                repaint();
//                Utils.mySleep(30);
//            }
//        }).start();
//    }
    public void reInitBot() {
        this.window.initBot();
    }


}
