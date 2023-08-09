package org.example.Swing;

import org.example.MainDataFlow.Bot;
import org.example.Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class CreateBotPanel extends JPanel implements ActionListener {
    public static final int[] BOUNDS = ControlPanel.CREATE_BOT_PANEL_BOUNDS;
    public static final int LABEL_WIDTH = BOUNDS[2] / 2 - ControlPanel.SPACE * 3 / 2;
    private final JCheckBox[] checkBoxes;
    private final JButton applyButton;
    private final Queue<Integer> chosenBoxes;
    private final int[] applyButtonBounds = {ControlPanel.SPACE, BOUNDS[3] - ControlPanel.LABELS_HEIGHT - ControlPanel.SPACE, LABEL_WIDTH, ControlPanel.LABELS_HEIGHT};
    private final String applyButtonText = "apply";
    private final Font applyButtonFont = new Font("Arial", Font.PLAIN, 20);
    private final ControlPanel controlPanel;
    private final JLabel isBotRunningLabel;
    private final JButton activeBotButton;

    public CreateBotPanel(ControlPanel jPanelFather) {
        Utils.initPanel(BOUNDS[0], BOUNDS[1], BOUNDS[2], BOUNDS[3], "Bot Control Panel", this, jPanelFather);
        this.controlPanel = jPanelFather;
        Utils.initButton(applyButtonBounds, applyButtonText, applyButtonFont, this, this, applyButton = new JButton());
        this.applyButton.setEnabled(false);
        this.chosenBoxes = new LinkedList<>();
        this.checkBoxes = new JCheckBox[Bot.Query.values().length];
        for (int i = 0; i < checkBoxes.length; i++) {
            this.checkBoxes[i] = new JCheckBox();
            this.checkBoxes[i].setFont(ControlPanel.TEXT_FONT);
            this.checkBoxes[i].addActionListener(this);
            Utils.initCheckBox(ControlPanel.SPACE, ControlPanel.SPACE * (i + 1) + ControlPanel.LABELS_HEIGHT * i, LABEL_WIDTH, ControlPanel.LABELS_HEIGHT, Bot.Query.values()[i].toString(), checkBoxes[i], this);
        }
        Utils.initJLabel(this.applyButton.getX() + this.applyButton.getWidth() + ControlPanel.SPACE, this.applyButton.getY() - 100, BOUNDS[2] - this.applyButton.getX() - this.applyButton.getWidth() - 2 * ControlPanel.SPACE, 100, "checking status", this.isBotRunningLabel = new JLabel(), this, SwingConstants.CENTER, SwingConstants.BOTTOM);
        Utils.initButton(new int[]{isBotRunningLabel.getX(), isBotRunningLabel.getY() + isBotRunningLabel.getHeight(), isBotRunningLabel.getWidth(), ControlPanel.LABELS_HEIGHT}, "checking data", applyButtonFont, this, this, activeBotButton = new JButton());
        paintIndefinitely();

    }

    public void paintIndefinitely() {
        new Thread(() -> {
            while (true) {
                this.repaint();
                Utils.mySleep(ControlPanel.SLEEP_TIME);
            }
        }).start();
    }


    public int addToChosenBoxesAndReturnDeletedIndex(int chosenBoxIndex) {
        if (chosenBoxes.contains(chosenBoxIndex)) {
            chosenBoxes.remove(chosenBoxIndex);
            return -1;
        }
        if (chosenBoxes.size() < 3) {
            chosenBoxes.add(chosenBoxIndex);
            return -1;
        }
        chosenBoxes.add(chosenBoxIndex);
        return chosenBoxes.remove();
    }

    public void submitQueries() {
        Bot.setActivatedQueries(chosenBoxes.stream().map(index -> Bot.Query.values()[index]).toList());
    }

    public boolean handelCheckBoxes(ActionEvent e) {
        if (!Arrays.stream(checkBoxes).toList().contains(e.getSource()))
            return !Bot.getActivatedQueries().equals(this.chosenBoxes.stream().map(index2 -> Bot.Query.values()[index2]).toList());
        int index = Arrays.stream(checkBoxes).toList().indexOf(e.getSource());
        int toRemove = this.addToChosenBoxesAndReturnDeletedIndex(index);
        if (toRemove != -1)
            this.checkBoxes[toRemove].setSelected(false);
        return !Bot.getActivatedQueries().equals(this.chosenBoxes.stream().map(index2 -> Bot.Query.values()[index2]).toList());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.activeBotButton.isEnabled()) {
            if (!Bot.isBotInitialized()) {
                this.isBotRunningLabel.setText(Utils.getHtmlText("Bot not connected.\nPlease active bot", "style='text-align: center;'"));
                this.activeBotButton.setText("Active bot");
                this.activeBotButton.setEnabled(true);
            } else if (Bot.isInternetProblem()) {
                this.isBotRunningLabel.setText(Utils.getHtmlText("Bot not connected.\nYou have no internet\nTry again later", "style='text-align: center;'"));
                this.activeBotButton.setText("Try again");
                this.activeBotButton.setEnabled(true);
            } else {
                if (!Bot.isBotFound()) {
                    this.isBotRunningLabel.setText(Utils.getHtmlText("bot not found.\nrestart the program\nand try again later", "style='text-align: center;'"));
                    this.activeBotButton.setText("Restart bot");
                    this.activeBotButton.setEnabled(false);
                } else {
                    if (Bot.isRunning()) {
                        this.isBotRunningLabel.setText("bot is running");
                        this.activeBotButton.setText("stop bot");
                        this.activeBotButton.setEnabled(true);
                    } else {
                        this.isBotRunningLabel.setText("bot not running");
                        this.activeBotButton.setText("activate bot");
                        this.activeBotButton.setEnabled(true);
                    }
                }
            }
        } else {
            this.isBotRunningLabel.setText("connecting");
            this.activeBotButton.setText("");
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.applyButton) {
            this.submitQueries();
            this.applyButton.setEnabled(false);
            return;
        }
        if (e.getSource() == this.activeBotButton) {
            new Thread(() -> {
                this.activeBotButton.setEnabled(false);
                if (!Bot.isBotInitialized()) {
                    Bot.setBotInitialized(true);
                    this.controlPanel.reInitBot();

                } else if (Bot.isInternetProblem()) {
                    this.controlPanel.reInitBot();

                } else
                    Bot.setRunning(!Bot.isRunning());
                this.activeBotButton.setEnabled(true);
                boolean toUpdateApplyButton = handelCheckBoxes(e);
                this.applyButton.setEnabled(toUpdateApplyButton && Bot.isRunning() && Bot.isBotInitialized() && !Bot.isInternetProblem() && Bot.isBotFound());
            }).start();

        }
        boolean toUpdateApplyButton = handelCheckBoxes(e);
        this.applyButton.setEnabled(toUpdateApplyButton && Bot.isRunning() && Bot.isBotInitialized() && !Bot.isInternetProblem() && Bot.isBotFound());
    }
}
