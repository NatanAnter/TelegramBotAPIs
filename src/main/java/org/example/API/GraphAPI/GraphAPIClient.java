package org.example.API.GraphAPI;

import org.example.Utils.Utils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class GraphAPIClient {
    private static final String URL = "https://quickchart.io/chart?";

    public static BufferedImage getGraph(List<String> labels, List<DataSet> dataSets, String header, JLabel jLabel) {
        return Utils.fetchChartImage(buildLinesGraphURL(labels, dataSets, header, jLabel));
    }

    public static String buildLinesGraphURL(List<String> labels, List<DataSet> dataSets, String header, JLabel jLabel) {
        return URL +
                "width=" + jLabel.getWidth() +
                "&height=" + jLabel.getHeight() +
                "&chart={" +
                "type: 'bar'," +
                "data: {" +
                "labels: " + Utils.listToHTMLString(labels) + ", " +
                "datasets: " + dataSets +
                "}, " +
                "options: {" +
                "  plugins: {" +
                "      legend: {" +
                "        display: false," +
                "        }," +
                "        title: {" +
                "        display: true," +
                "        text: '" + header + "'," +
//                "        font: {" +
//                "          size: 20," +
//                "        }," +
                "      }," +
                "    }," +
                "  }" +
                "}";

    }
}
