package org.example.API.GraphAPI;

import org.example.Utils.Utils;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataSet {
    private String type;
    private String label;
    private Color borderColor;
    private int borderWidth;
    private boolean fill;
    private List<Integer> data;
    private double lineTension;
    private Color backgroundColor;
    private int pointRadius;
    private boolean toAccumulate;

    public DataSet(String type, String label, Color borderColor, int borderWidth, boolean fill, List<Integer> data, double lineTension, Color backgroundColor, int pointRadius, boolean toAccumulate) {
        this.type = type;
        this.label = label;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.fill = fill;
        this.data = data;
        this.lineTension = lineTension;
        this.backgroundColor = backgroundColor;
        this.pointRadius = pointRadius;
        this.toAccumulate = toAccumulate;
        if (this.toAccumulate)
            this.data = accumulate(this.data);
    }

    public DataSet(String type, String label, Color borderColor, int borderWidth, boolean fill, List<Integer> data, Color backgroundColor, int pointRadius, boolean toAccumulate) {
        this.type = type;
        this.label = label;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.fill = fill;
        this.data = data;
        this.lineTension = toAccumulate ? 0 : 0.4;
        this.backgroundColor = backgroundColor;
        this.pointRadius = pointRadius;
        this.toAccumulate = toAccumulate;
        if (this.toAccumulate)
            this.data = accumulate(this.data);
    }

    public static List<Integer> accumulate(List<Integer> input) {
        return IntStream.range(0, input.size())
                .mapToObj(i -> input.subList(0, i + 1).stream().mapToInt(Integer::intValue).sum())
                .collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return "{" +
                "type:'" + type + "' " +
                ", label: '" + label + "'" +
                ", borderColor: " + Utils.colorToHTMLString(borderColor) +
                ", backgroundColor: " + Utils.colorToHTMLString(backgroundColor) +
                ", borderWidth: " + borderWidth +
                ", fill: " + fill +
                ", data: " + Utils.listToHTMLString(data) +
                ", lineTension: " + lineTension +
                ", pointRadius: " + pointRadius +

                "}";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

    public double getLineTension() {
        return lineTension;
    }

    public void setLineTension(double lineTension) {
        this.lineTension = lineTension;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isToAccumulate() {
        return toAccumulate;
    }

    public void setToAccumulate(boolean toAccumulate) {
        this.toAccumulate = toAccumulate;
    }
}
