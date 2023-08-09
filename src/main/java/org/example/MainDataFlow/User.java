package org.example.MainDataFlow;

import org.example.API.JokesAPI.JokeAPIClient;
import org.example.API.NewsAPI.NewsAPIClient;
import org.example.Swing.Window;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User implements Comparable<User> {
    private long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime signUpDate;
    private int numberOfMessages;
    private int numberOfActions;
    //jokes
    private List<JokeAPIClient.Categories> preferredJokeCategories;
    private List<JokeAPIClient.Categories> tempPreferredJokeCategories;
    private List<JokeAPIClient.Flags> unwantedFlags;
    private List<JokeAPIClient.Flags> wantedFlags;
    private List<JokeAPIClient.Flags> tempUnwantedFlags;
    private List<JokeAPIClient.Flags> tempWantedFlags;
    private String currentUnwantedFlagsPollId;
    private String currentWantedFlagsPollId;
    private String currentCategoriesPollId;
    //news
    private String preferredSourcedName;
    private String preferredSource;
    private NewsAPIClient.Category preferredCategory1;
    private NewsAPIClient.Country preferredCategory1Country;
    private NewsAPIClient.Category preferredCategory2;
    private NewsAPIClient.Country preferredCategory2Country;
    private String currentNewsCategoriesPollId;
    private int numOfNews;
    //country
    private boolean toSendFlag;
    private Phase phase;

    public User() {
    }

    public User(long id, LocalDateTime signUpDate) {
        this.id = id;
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = "";
        this.signUpDate = signUpDate;
        this.numberOfMessages = 1;
        this.numberOfActions = 0;
        this.preferredJokeCategories = new ArrayList<>();
        this.unwantedFlags = new ArrayList<>();
        this.wantedFlags = new ArrayList<>();
        this.preferredSource = "";
        this.currentNewsCategoriesPollId = "";
        this.preferredCategory1 = NewsAPIClient.Category.myCategory;
        this.preferredCategory2 = NewsAPIClient.Category.myCategory;
        this.preferredCategory1Country = NewsAPIClient.Country.noCountry;
        this.preferredCategory2Country = NewsAPIClient.Country.noCountry;
        this.preferredSourcedName = "my domain";
        this.numOfNews = NewsAPIClient.TIMES_DEFAULT;
        this.toSendFlag = false;
        this.phase = new Phase(Phase.NewUser.firstLine);
        this.currentCategoriesPollId = "";
        this.currentUnwantedFlagsPollId = "";
        this.currentWantedFlagsPollId = "";
        this.tempUnwantedFlags = new ArrayList<>();
        this.tempWantedFlags = new ArrayList<>();
        this.tempPreferredJokeCategories = new ArrayList<>();
    }

    @Override
    public int compareTo(User otherUser) {
        return (int) (this.id - otherUser.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getId() == user.getId();
    }

    public List<NewsAPIClient.Category> getUsedCategories() {
        return List.of(preferredCategory1, preferredCategory2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " ,id: " + id + " sign up: " + signUpDate.format(Window.FORMAT_DATE_TIME);

    }

    public void addMessage() {
        this.numberOfMessages++;
    }

    public void addAction() {
        this.numberOfActions++;
    }

    public void resetTempPreferences() {
        this.tempUnwantedFlags = new ArrayList<>();
        this.tempWantedFlags = new ArrayList<>();
        this.tempPreferredJokeCategories = new ArrayList<>();
    }

    public boolean isNoPollVoted() {
        return (this.tempUnwantedFlags.isEmpty() && this.tempWantedFlags.isEmpty() && this.tempPreferredJokeCategories.isEmpty());
    }

    public boolean hasNoPreferences() {
        return (this.unwantedFlags.isEmpty() && this.wantedFlags.isEmpty() && this.preferredJokeCategories.isEmpty());
    }

    public Enum<?> getPhase() {
        return phase.getPhase();
    }

    public void setPhase(Enum<?> phase) {
        this.phase.setPhase(phase);
    }

    public String getCurrentUnwantedFlagsPollId() {
        return currentUnwantedFlagsPollId;
    }

    public void setCurrentUnwantedFlagsPollId(String currentUnwantedFlagsPollId) {
        this.currentUnwantedFlagsPollId = currentUnwantedFlagsPollId;
    }

    public String getCurrentWantedFlagsPollId() {
        return currentWantedFlagsPollId;
    }

    public void setCurrentWantedFlagsPollId(String currentWantedFlagsPollId) {
        this.currentWantedFlagsPollId = currentWantedFlagsPollId;
    }

    public String getCurrentCategoriesPollId() {
        return currentCategoriesPollId;
    }

    public void setCurrentCategoriesPollId(String currentCategoriesPollId) {
        this.currentCategoriesPollId = currentCategoriesPollId;
    }

    public boolean isToSendFlag() {
        return toSendFlag;
    }

    public void setToSendFlag(boolean toSendFlag) {
        this.toSendFlag = toSendFlag;
    }

    public List<JokeAPIClient.Flags> getTempUnwantedFlags() {
        return tempUnwantedFlags;
    }

    public void setTempUnwantedFlags(List<JokeAPIClient.Flags> tempUnwantedFlags) {
        this.tempUnwantedFlags = tempUnwantedFlags;
    }

    public List<JokeAPIClient.Categories> getTempPreferredJokeCategories() {
        return tempPreferredJokeCategories;
    }

    public void setTempPreferredJokeCategories(List<JokeAPIClient.Categories> tempPreferredJokeCategories) {
        this.tempPreferredJokeCategories = tempPreferredJokeCategories;
    }

    public List<JokeAPIClient.Flags> getTempWantedFlags() {
        return tempWantedFlags;
    }

    public void setTempWantedFlags(List<JokeAPIClient.Flags> tempWantedFlags) {
        this.tempWantedFlags = tempWantedFlags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(LocalDateTime signUpDate) {
        this.signUpDate = signUpDate;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public int getNumberOfActions() {
        return numberOfActions;
    }

    public void setNumberOfActions(int numberOfActions) {
        this.numberOfActions = numberOfActions;
    }

    public List<JokeAPIClient.Categories> getPreferredJokeCategories() {
        return preferredJokeCategories;
    }

    public void setPreferredJokeCategories(List<JokeAPIClient.Categories> preferredJokeCategories) {
        this.preferredJokeCategories = preferredJokeCategories;
    }

    public List<JokeAPIClient.Flags> getUnwantedFlags() {
        return unwantedFlags;
    }

    public void setUnwantedFlags(List<JokeAPIClient.Flags> unwantedFlags) {
        this.unwantedFlags = unwantedFlags;
    }

    public List<JokeAPIClient.Flags> getWantedFlags() {
        return wantedFlags;
    }

    public void setWantedFlags(List<JokeAPIClient.Flags> wantedFlags) {
        this.wantedFlags = wantedFlags;
    }

    public String getPreferredSource() {
        return preferredSource;
    }

    public void setPreferredSource(String preferredSource) {
        this.preferredSource = preferredSource;
    }

    public int getNumOfNews() {
        return numOfNews;
    }

    public void setNumOfNews(int numOfNews) {
        this.numOfNews = numOfNews;
    }

    public String getPreferredSourcedName() {
        return preferredSourcedName;
    }

    public void setPreferredSourcedName(String preferredSourcedName) {
        this.preferredSourcedName = preferredSourcedName;
    }

    public NewsAPIClient.Category getPreferredCategory1() {
        return preferredCategory1;
    }

    public void setPreferredCategory1(NewsAPIClient.Category preferredCategory1) {
        this.preferredCategory1 = preferredCategory1;
    }

    public NewsAPIClient.Country getPreferredCategory1Country() {
        return preferredCategory1Country;
    }

    public void setPreferredCategory1Country(NewsAPIClient.Country preferredCategory1Country) {
        this.preferredCategory1Country = preferredCategory1Country;
    }

    public NewsAPIClient.Category getPreferredCategory2() {
        return preferredCategory2;
    }

    public void setPreferredCategory2(NewsAPIClient.Category preferredCategory2) {
        this.preferredCategory2 = preferredCategory2;
    }

    public NewsAPIClient.Country getPreferredCategory2Country() {
        return preferredCategory2Country;
    }

    public void setPreferredCategory2Country(NewsAPIClient.Country preferredCategory2Country) {
        this.preferredCategory2Country = preferredCategory2Country;
    }

    public String getCurrentNewsCategoriesPollId() {
        return currentNewsCategoriesPollId;
    }

    public void setCurrentNewsCategoriesPollId(String currentNewsCategoriesPollId) {
        this.currentNewsCategoriesPollId = currentNewsCategoriesPollId;
    }
}
