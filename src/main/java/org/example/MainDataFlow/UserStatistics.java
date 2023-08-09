package org.example.MainDataFlow;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserStatistics {
    private static Map<Long, User> users;
    private static List<Activity> activitiesHistory;

    public UserStatistics() {
        UserStatistics.users = new HashMap<>();
        UserStatistics.activitiesHistory = new ArrayList<>();
    }

    public static long getNumOfMessages() {
        return activitiesHistory.stream().filter(activity -> !activity.isQueryType()).count();
    }

    public static long getNumOfQueries() {
        return activitiesHistory.stream().filter(Activity::isQueryType).count();
    }

    public static Bot.Query getMostPopularQuery() {
        return UserStatistics.getActivitiesHistory().stream().filter(Activity::isQueryType).collect(Collectors.groupingBy(Activity::getApi, Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }

    public static User getFirstUser() {
        return UserStatistics.users.values().stream().min(Comparator.comparing(User::getSignUpDate)).orElse(null);

    }

    public static void insertActivity(Activity activity) {
        UserStatistics.activitiesHistory.add(activity);
    }

    public static LocalDateTime getSignUpDate(User user) {
        if (user == null) return LocalDateTime.now();
        return user.getSignUpDate();
    }

    public static void insertNewUser(Long id) {
        users.put(id, new User(id, LocalDateTime.now()));
    }

    public static void insertNewUser(Long id, LocalDateTime date) {
        users.put(id, new User(id, date));
    }

    public static int getNumOfUsers() {
        return users.size();
    }

    public static String getNameORId(User user) {
        if (user == null) return "";
        String re = "";
        if (!Objects.equals(user.getFirstName(), "")) re += user.getFirstName() + " ";
        if (!Objects.equals(user.getLastName(), "")) re += user.getLastName() + " ";
        return re + "id: " + user.getId();

    }

    public static int getNumberOfMessages(User user) {
        if (user == null) return 0;
        return user.getNumberOfMessages();

    }

    public static User getMostActiveUser() {
        return users.values().stream().max(Comparator.comparingInt(User::getNumberOfMessages)).orElse(null);
    }

    public static User getUser(long id) {
        return users.get(id);
    }

    private static int getUsersBetweenDates(LocalDateTime startingTime, LocalDateTime endTime) {
        return users.values().stream().filter(user -> user.getSignUpDate().isAfter(startingTime) || user.getSignUpDate().isEqual(startingTime)).filter(user -> user.getSignUpDate().isBefore(endTime)).toList().size();
    }

    public static List<LocalDateTime> divideDates(LocalDateTime startDate, LocalDateTime endDate, int times) {
        long duration = ChronoUnit.NANOS.between(startDate, endDate);
        long interval = duration / (times);
        return IntStream.rangeClosed(0, times).mapToObj(i -> startDate.plusNanos(interval * i)).collect(Collectors.toList());
    }

    public static List<Integer> getNewUsersCountForGraph(LocalDateTime startingTime, LocalDateTime endTime, int points) {
        List<LocalDateTime> intervals = divideDates(startingTime, endTime, points);
        return IntStream.range(0, intervals.size() - 1).mapToObj(index -> getUsersBetweenDates(intervals.get(index), intervals.get(index + 1))).toList();
    }


    public static int getQueriesBetweenDates(LocalDateTime startingTime, LocalDateTime endTime, Bot.Query query) {
        return UserStatistics.activitiesHistory.stream().filter(Activity::isQueryType).filter(activity -> activity.getApi() == query).filter(activity -> activity.getDateTime().isAfter(startingTime) || activity.getDateTime().isEqual(startingTime)).filter(activity -> activity.getDateTime().isBefore(endTime)).toList().size();
    }

    public static List<Integer> getQueriesCountForGraph(LocalDateTime startingTime, LocalDateTime endTime, int points, Bot.Query query) {
        List<LocalDateTime> intervals = divideDates(startingTime, endTime, points);
        return IntStream.range(0, intervals.size() - 1).mapToObj(index -> getQueriesBetweenDates(intervals.get(index), intervals.get(index + 1), query)).toList();
    }

    public static Map<Bot.Query, List<Integer>> getQueriesCountForGraph(LocalDateTime startingTime, LocalDateTime endTime, int points) {
        List<LocalDateTime> intervals = divideDates(startingTime, endTime, points);
        Map<Bot.Query, List<Integer>> map = new HashMap<>();
        Arrays.stream(Bot.Query.values()).forEach(query -> map.put(query, IntStream.range(0, intervals.size() - 1).mapToObj(index -> getQueriesBetweenDates(intervals.get(index), intervals.get(index + 1), query)).toList()));
        return map;
    }

    public static int getMessagesBetweenDates(LocalDateTime startingTime, LocalDateTime endTime) {
        return UserStatistics.activitiesHistory.stream().filter(activity -> activity.getDateTime().isAfter(startingTime) || activity.getDateTime().isEqual(startingTime)).filter(activity -> activity.getDateTime().isBefore(endTime)).toList().size();
    }

    public static List<Integer> getMessagesForGraph(LocalDateTime startingTime, LocalDateTime endTime, int points) {
        List<LocalDateTime> intervals = divideDates(startingTime, endTime, points);
        return IntStream.range(0, intervals.size() - 1).mapToObj(index -> getMessagesBetweenDates(intervals.get(index), intervals.get(index + 1))).toList();
        //try
    }

    public static Map<Long, User> getUsers() {
        return users;
    }

    public static void setUsers(Map<Long, User> users1) {
        users = users1;
    }

    public static List<Activity> getActivitiesHistory() {
        return activitiesHistory;
    }

    public static void setActivitiesHistory(List<Activity> activitiesHistory1) {
        activitiesHistory = activitiesHistory1;
    }

}
