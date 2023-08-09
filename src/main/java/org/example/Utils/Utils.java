package org.example.Utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.Swing.ControlPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.lang.Thread.sleep;

public class Utils {
    public static void mySleep(int mills) {
        try {
            sleep(mills);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage fetchChartImage(String imageUrl) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String chartConfiguration = imageUrl.substring(imageUrl.indexOf('{'));
            String encodedChartConfiguration = URLEncoder.encode(chartConfiguration, StandardCharsets.UTF_8);
            String chartUrl = imageUrl.substring(0, imageUrl.indexOf('{')) + encodedChartConfiguration;
            HttpGet httpGet = new HttpGet(chartUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                byte[] imageBytes = EntityUtils.toByteArray(response.getEntity());
                return ImageIO.read(new ByteArrayInputStream(imageBytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getHtmlText(String text, String div) {
        return "<html><div " + div + ">" + text.replaceAll("\n", "<br>") + "</div></html>";
    }

    public static String listToHTMLString(List<?> list) {
        if (list.size() == 0)
            return "[]";
        String str = "['" + list.get(0) + "'";
        for (int i = 1; i < list.size(); i++) {
            str += ", '" + list.get(i) + "'";
        }
        return str + "]";
    }

    public static String colorToHTMLString(Color color) {
        return String.format("'rgb(%s,%s,%s)'", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static ImageIcon scaleImage(Image image, int width, int height) {
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public static void initJLabel(int x, int y, int width, int height, String str, JLabel jLabel, JPanel jPanel, int horizontalAlignment, int verticalAlignment) {
        jLabel.setText(str);
        jLabel.setFont(ControlPanel.TEXT_FONT);
        jLabel.setBounds(x, y, width, height);
        jLabel.setHorizontalAlignment(horizontalAlignment);
        jLabel.setVerticalAlignment(verticalAlignment);
        jPanel.add(jLabel);
    }

    public static void initJLabel(int x, int y, int width, int height, BufferedImage bufferedImage, JLabel jLabel, JPanel jPanel, int horizontalAlignment, int verticalAlignment) {
        jLabel.setBounds(x, y, width, height);
        jLabel.setIcon(scaleImage(bufferedImage, jLabel.getWidth(), jLabel.getHeight()));
        jLabel.setFont(ControlPanel.TEXT_FONT);
        jLabel.setHorizontalAlignment(horizontalAlignment);
        jLabel.setVerticalAlignment(verticalAlignment);
        jPanel.add(jLabel);
    }

    public static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000); // Set the timeout for the connection in milliseconds

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false; //no internet connection available
        }
    }

    public static void initPanel(int x, int y, int width, int height, String str, JPanel jPanelSon, JPanel jPanelFather) {
        jPanelSon.setBackground(Color.LIGHT_GRAY);
        jPanelSon.setLayout(null);
        jPanelSon.setBounds(x, y, width, height);
        jPanelSon.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(str),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        jPanelFather.add(jPanelSon);
    }

    public static void initCheckBox(int x, int y, int width, int height, String str, JCheckBox checkBox, JPanel jPanel) {
        checkBox.setText(str);
        checkBox.setBounds(x, y, width, height);
        jPanel.add(checkBox);

    }

    public static void initButton(int[] bounds, String text, Font font, JPanel panel, ActionListener actionListenerPanel, JButton button) {
        button.setText(text);
        button.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
        button.setFont(font);
        button.addActionListener(actionListenerPanel);
        button.setFocusable(false);
        panel.add(button);
    }

    public static void initButton(int[] bounds, Icon icon, JPanel panel, ActionListener actionListenerPanel, JButton button) {
        button.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
        button.setIcon(icon);
        button.addActionListener(actionListenerPanel);
        button.setFocusable(false);
        panel.add(button);
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
