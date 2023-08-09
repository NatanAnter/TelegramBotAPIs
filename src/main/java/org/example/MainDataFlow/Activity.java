package org.example.MainDataFlow;

import org.example.Swing.Window;

import java.time.LocalDateTime;
import java.util.List;

public class Activity {

    private Long userId;
    private Bot.Query api;
    private boolean queryType;
    private LocalDateTime dateTime;


    public Activity(Long userId, Bot.Query api, LocalDateTime dateTime) {
        this.userId = userId;
        this.api = api;
        this.dateTime = dateTime;
        this.queryType = true;
    }

    public Activity(Long userId, LocalDateTime dateTime) {
        this.userId = userId;
        this.queryType = false;
        this.dateTime = dateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Bot.Query getApi() {
        return api;
    }

    public void setApi(Bot.Query api) {
        this.api = api;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isQueryType() {
        return queryType;
    }

    public void setQueryType(boolean queryType) {
        this.queryType = queryType;
    }

    public List<String> toListString() {
        return List.of("User: " + UserStatistics.getNameORId(UserStatistics.getUser(userId)), " Activity: " + api, " time: " + dateTime.format(Window.FORMAT_DATE_TIME));
    }
}
