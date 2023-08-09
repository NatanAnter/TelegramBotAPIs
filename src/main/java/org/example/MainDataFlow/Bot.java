package org.example.MainDataFlow;

import org.example.API.CatAPI.CatAPIClient;
import org.example.API.CountryAPI.CountryAPIClient;
import org.example.API.CountryAPI.CountryModel;
import org.example.API.JokesAPI.JokeAPIClient;
import org.example.API.JokesAPI.JokesModel;
import org.example.API.NewsAPI.NewsAPIClient;
import org.example.API.NewsAPI.NewsModel;
import org.example.API.NewsAPI.Source;
import org.example.API.NumbersAPI.NumbersAPIClient;
import org.example.Swing.GraphsPanel;
import org.example.Utils.Utils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bot extends TelegramLongPollingBot {
    public static final String BOT_TOKEN = "6393630545:AAEKRPZ0xQzdub06gtI6eoOk3yYXmo1d7LE";
    public static final int OTHER_TYPE = -1;
    public static final int MESSAGE_TYPE = 1;
    public static final int BUTTON_TYPE = 2;
    public static final int POLL_TYPE = 3;
    private static boolean running;
    private static boolean botInitialized = false;
    private static boolean internetProblem = true;
    private static boolean botFound;
    private static List<Bot.Query> activatedQueries;
    private GraphsPanel graphsPanel;

    public Bot() {
        running = false;
        activatedQueries = new ArrayList<>();
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Bot.running = running;
    }

    public static List<Bot.Query> getActivatedQueries() {
        return activatedQueries;
    }

    public static void setActivatedQueries(List<Bot.Query> activatedQueries) {
        Bot.activatedQueries = activatedQueries;
    }

    public static void run() {
        Bot.running = true;
    }

    public static boolean isBotInitialized() {
        return botInitialized;
    }

    public static void setBotInitialized(boolean botInitialized) {
        Bot.botInitialized = botInitialized;
    }

    public static boolean isInternetProblem() {
        return internetProblem;
    }

    public static void setInternetProblem(boolean internetProblem) {
        Bot.internetProblem = internetProblem;
    }

    public static boolean isBotFound() {
        return botFound;
    }

    public static void setBotFound(boolean botFound) {
        Bot.botFound = botFound;
    }

    @Override
    public String getBotUsername() {
        return "AAC_natan_bot";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!Bot.running) return;
        this.graphsPanel.updateAll();
        int messageType = getMessageType(update);
        long chatId = getChatId(update);
        String text = getText(update);
        UserStatistics.insertActivity(new Activity(chatId, LocalDateTime.now()));
        if (chatId == 0 || messageType == -1) return;
        User currentUser = UserStatistics.getUser(chatId);
        if (currentUser == null) {//new user
            UserStatistics.insertNewUser(chatId, LocalDateTime.now());
            currentUser = UserStatistics.getUser(chatId);
            if (!update.getMessage().getFrom().getFirstName().equals(""))
                currentUser.setFirstName(update.getMessage().getFrom().getFirstName());
            if (!update.getMessage().getFrom().getLastName().equals(""))
                currentUser.setLastName(update.getMessage().getFrom().getLastName());
        } else {//old User
            currentUser.addMessage();
        }
        Enum<?> phase = currentUser.getPhase();
        if (phase == Phase.NewUser.firstLine) {
            mySendMessage(chatId, "Hello There!\nWelcome to the best bot.");
            sendHomeOptionsToUser(currentUser);
            return;
        }
        if (text.equalsIgnoreCase("return to home")) {
            mySendMessage(chatId, " returning to home");
            sendHomeOptionsToUser(currentUser);
            return;
        }
        if (phase == Phase.Home.home) {
            if (Objects.equals(text.toLowerCase(), "CAT FACTS".toLowerCase())) {
                currentUser.setPhase(Phase.CatAPI.first);
                catFactsAPI(currentUser);
            } else if (Objects.equals(text.toLowerCase(), "COUNTRIES DATA".toLowerCase())) {
                currentUser.setPhase(Phase.CountryAPI.first);
                countriesAPI(currentUser, messageType, text);
            } else if (Objects.equals(text.toLowerCase(), "FUNNY JOKE".toLowerCase())) {
                currentUser.setPhase(Phase.JokesAPI.first);
                jokesAPI(currentUser, messageType, text, update);
            } else if (Objects.equals(text.toLowerCase(), "NEWS".toLowerCase())) {
                currentUser.setPhase(Phase.NewsAPI.first);
                newsAPI(currentUser, messageType, text, update);
            } else if (Objects.equals(text.toLowerCase(), "NUMBERS FACTS".toLowerCase())) {
                currentUser.setPhase(Phase.NumbersAPI.first);
                numbersAPI(currentUser, text);
            } else {
                mySendMessage(chatId, "I'm sorry. there is no function like this right now. you can choose from those: ");
                sendHomeOptionsToUser(currentUser);
            }
            return;
        }
        if (phase instanceof Phase.CountryAPI) {
            countriesAPI(currentUser, messageType, text);
            return;
        }
        if (phase instanceof Phase.JokesAPI) {
            jokesAPI(currentUser, messageType, text, update);
            return;
        }
        if (phase instanceof Phase.NewsAPI) {
            newsAPI(currentUser, messageType, text, update);
            return;
        }
        if (phase instanceof Phase.NumbersAPI) {
            numbersAPI(currentUser, text);
            return;
        }
    }

    public void numbersAPI(User user, String text) {
        Enum<?> phase = user.getPhase();
        long chatId = user.getId();
        if (phase == Phase.NumbersAPI.first) {
            sendNumbersOptionToUser(user);
            return;
        }
        if (phase == Phase.NumbersAPI.specific_number && text.equalsIgnoreCase("return back")) {
            sendHomeOptionsToUser(user);
            return;
        }
        if (phase == Phase.NumbersAPI.specific_number && text.equalsIgnoreCase("random number")) {
            String response = new NumbersAPIClient().getRandomNumberFact();
            mySendMessage(chatId, response);
            UserStatistics.insertActivity(new Activity(chatId, Query.Numbers, LocalDateTime.now()));
            sendNumbersOptionToUser(user);
            return;
        }
        if (phase == Phase.NumbersAPI.specific_number && Utils.isInteger(text)) {
            String response = new NumbersAPIClient().getNumberFact(Integer.parseInt(text));
            mySendMessage(chatId, response);
            UserStatistics.insertActivity(new Activity(chatId, Query.Numbers, LocalDateTime.now()));
            sendNumbersOptionToUser(user);
            return;
        }
    }

    public void sendNumbersOptionToUser(User user) {
        user.setPhase(Phase.NumbersAPI.specific_number);
        sendMessageWithKeyboard(user.getId(), "write a number or choose random number", 1, "random number", "return back");
    }

    public void newsAPI(User user, int massageType, String text, Update update) {
        SendMessage sendMessage = new SendMessage();
        long chatId = user.getId();
        sendMessage.setChatId(chatId);
        Enum<?> phase = user.getPhase();
        if (text.equalsIgnoreCase("do nothing")) {
            mySendMessage(chatId, "sure. keep going");
            return;
        }
        if (phase == Phase.NewsAPI.first) {
            user.setPhase(Phase.NewsAPI.main);
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.main && text.equalsIgnoreCase("return back")) {
            sendHomeOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.main && text.equalsIgnoreCase(user.getPreferredSourcedName())) {
            if (user.getPreferredSource().equals("")) {
                user.setPhase(Phase.NewsAPI.setting_site_main);
                mySendMessage(chatId, "you haven't set your preferred source yet. \ndo it now!");
                sendNewsSiteSettingName(chatId);
                return;
            }
            user.setPhase(Phase.NewsAPI.main);
            NewsAPIClient newsAPIClient = new NewsAPIClient();
            NewsModel newsModel = newsAPIClient.searchBySourceAPI(user.getPreferredSource(), user.getNumOfNews());
            sendNewsToUser(newsModel, chatId);
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.main && user.getPreferredCategory1().toString().equalsIgnoreCase(text)) {
            if (user.getPreferredCategory1().equals(NewsAPIClient.Category.myCategory)) {
                user.setPhase(Phase.NewsAPI.setting_category1);
                mySendMessage(chatId, "you haven't set your preferred category yet. \ndo it now!");
                sendNewsCategoriesOptionsToUser(user);
                return;
            }
            user.setPhase(Phase.NewsAPI.main);
            NewsAPIClient newsAPIClient = new NewsAPIClient();
            NewsModel newsModel = newsAPIClient.searchByCountryAndCategoryAPI(user.getPreferredCategory1Country(), user.getPreferredCategory1(), user.getNumOfNews());
            sendNewsToUser(newsModel, chatId);
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.main && user.getPreferredCategory2().toString().equalsIgnoreCase(text)) {
            if (user.getPreferredCategory2().equals(NewsAPIClient.Category.myCategory)) {
                user.setPhase(Phase.NewsAPI.setting_category2);
                mySendMessage(chatId, "you haven't set your preferred category yet. \ndo it now!");
                sendNewsCategoriesOptionsToUser(user);
                return;
            }
            user.setPhase(Phase.NewsAPI.main);
            NewsAPIClient newsAPIClient = new NewsAPIClient();
            NewsModel newsModel = newsAPIClient.searchByCountryAndCategoryAPI(user.getPreferredCategory2Country(), user.getPreferredCategory2(), user.getNumOfNews());
            sendNewsToUser(newsModel, chatId);
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.main && text.equalsIgnoreCase("setting")) {
            user.setPhase(Phase.NewsAPI.settings_main);
            sendNewsSettingOptionsToUser(chatId);
            return;
            //"domain", "category1", "category2", "number of news","return back"
        }
        if (phase == Phase.NewsAPI.settings_main && text.equalsIgnoreCase("domain")) {
            user.setPhase(Phase.NewsAPI.setting_site_main);
            sendNewsSiteSettingName(chatId);
            //"sport-5", "Ynet", "costume", "return back"////////////////
            return;
        }
        if (phase == Phase.NewsAPI.setting_site_main && text.equalsIgnoreCase("israel google news")) {
            user.setPhase(Phase.NewsAPI.main);
            user.setPreferredSource("google-news-is");
            user.setPreferredSourcedName("google news");
            mySendMessage(chatId, "Great! your setting updated. click \"google news\" to see news from israel");
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.setting_site_main && text.equalsIgnoreCase("Ynet")) {
            user.setPhase(Phase.NewsAPI.main);
            user.setPreferredSource("ynet");
            user.setPreferredSourcedName("ynet");
            mySendMessage(chatId, "Great! your setting updated. click ynet to see news from ynet");
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.setting_site_main && text.equalsIgnoreCase("costume")) {
            List<String> sources = Arrays.stream(new NewsAPIClient().getAllSources().getSources()).map(Source::getId).toList();
            mySendMessage(chatId, "these are the sources you can choose from:\n" + sources + "\nplease write one from the above");
            user.setPhase(Phase.NewsAPI.setting_site_source);
            return;
        }
        if (phase == Phase.NewsAPI.setting_site_main && text.equalsIgnoreCase("return back")) {
            user.setPhase(Phase.NewsAPI.settings_main);
            sendNewsSettingOptionsToUser(chatId);
            return;
        }
        if (phase == Phase.NewsAPI.setting_site_source) {
            List<String> sources = Arrays.stream(new NewsAPIClient().getAllSources().getSources()).map(Source::getId).toList();
            if (!sources.contains(text.toLowerCase())) {
                mySendMessage(chatId, "your source must be from: \n" + sources);
                return;
            }
            NewsModel newsModel = new NewsAPIClient().searchBySourceAPI(text.toLowerCase(), user.getNumOfNews());
            if (newsModel == null || newsModel.getArticles() == null) {
                mySendMessage(chatId, "sorry, this is not working\ntry again");
                user.setPhase(Phase.NewsAPI.settings_main);
                sendNewsSettingOptionsToUser(chatId);
                return;
            }
            user.setPhase(Phase.NewsAPI.setting_site_name);
            user.setPreferredSource(text.toLowerCase());
            mySendMessage(chatId, "please write a name for your source");
            return;
        }
        if (phase == Phase.NewsAPI.setting_site_name) {
            user.setPreferredSourcedName(text);
            user.setPhase(Phase.NewsAPI.main);
            mySendMessage(chatId, "great! this is working. your setting saved");
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.settings_main && text.equalsIgnoreCase("category1")) {
            user.setPhase(Phase.NewsAPI.setting_category1);
            sendNewsCategoriesOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.setting_category1) {
            List<NewsAPIClient.Category> categoriesList = Arrays.stream(NewsAPIClient.Category.values()).filter(category -> !category.equals(NewsAPIClient.Category.myCategory) && !user.getUsedCategories().contains(category)).toList();
            NewsAPIClient.Category category = categoriesList.get(update.getPollAnswer().getOptionIds().get(0));
            if (!categoriesList.contains(category)) {
                mySendMessage(chatId, "please choose category from the list. the options are: " + categoriesList + " you picked: " + category);
                sendNewsCategoriesOptionsToUser(user);
                return;
            }
            user.setPhase(Phase.NewsAPI.setting_category1_country);
            user.setPreferredCategory1(category);
            mySendMessage(chatId, "great, you chose: " + category + ". now write a country code for the category\nfor israel write il etc");
            return;
        }
        if (phase == Phase.NewsAPI.setting_category1_country) {
            List<String> countries = Arrays.stream(NewsAPIClient.Country.values()).filter(a -> !a.equals(NewsAPIClient.Country.noCountry)).map(NewsAPIClient.Country::toString).toList();
            if (!countries.contains(text.toLowerCase())) {
                mySendMessage(chatId, text + " is illegal country code. the options are: " + countries);
                mySendMessage(chatId, "please write the country code of the site as shown above");
                return;
            }
            NewsAPIClient newsAPIClient = new NewsAPIClient();
            NewsModel newsModel = newsAPIClient.searchByCountryAndCategoryAPI(NewsAPIClient.Country.valueOf(text.toLowerCase()), user.getPreferredCategory1(), user.getNumOfNews());
            if (newsModel == null) {
                user.setPhase(Phase.NewsAPI.settings_main);
                user.setPreferredCategory1(NewsAPIClient.Category.myCategory);
                mySendMessage(chatId, "there is a problem with your link or country.\n setting not saved. try again");
                sendNewsSettingOptionsToUser(chatId);
                return;
            }
            user.setPhase(Phase.NewsAPI.main);
            mySendMessage(chatId, "Great! your setting changed. you can click " + user.getPreferredCategory1() + " to see your news");
            user.setPreferredCategory1Country(NewsAPIClient.Country.valueOf(text.toLowerCase()));
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.setting_category2) {
            List<NewsAPIClient.Category> categoriesList = Arrays.stream(NewsAPIClient.Category.values()).filter(category -> !category.equals(NewsAPIClient.Category.myCategory) && !user.getUsedCategories().contains(category)).toList();
            NewsAPIClient.Category category = categoriesList.get(update.getPollAnswer().getOptionIds().get(0));
            if (!categoriesList.contains(category)) {
                mySendMessage(chatId, "please choose category from the list. the options are: " + categoriesList + " you picked: " + category);
                sendNewsCategoriesOptionsToUser(user);
                return;
            }
            user.setPhase(Phase.NewsAPI.setting_category2_country);
            user.setPreferredCategory2(category);
            mySendMessage(chatId, "great, you chose: " + category + ". now write a country code for the category\nfor israel write il etc");
            return;
        }
        if (phase == Phase.NewsAPI.setting_category2_country) {
            List<String> countries = Arrays.stream(NewsAPIClient.Country.values()).filter(a -> !a.equals(NewsAPIClient.Country.noCountry)).map(NewsAPIClient.Country::toString).toList();
            if (!countries.contains(text.toLowerCase())) {
                mySendMessage(chatId, text + " is illegal country code. the options are: " + countries);
                mySendMessage(chatId, "please write the country code of the site as shown above");
                return;
            }
            NewsAPIClient newsAPIClient = new NewsAPIClient();
            NewsModel newsModel = newsAPIClient.searchByCountryAndCategoryAPI(NewsAPIClient.Country.valueOf(text.toLowerCase()), user.getPreferredCategory2(), user.getNumOfNews());
            if (newsModel == null) {
                user.setPhase(Phase.NewsAPI.settings_main);
                user.setPreferredCategory1(NewsAPIClient.Category.myCategory);
                mySendMessage(chatId, "there is a problem with your link or country.\n setting not saved. try again");
                sendNewsSettingOptionsToUser(chatId);
                return;
            }
            user.setPhase(Phase.NewsAPI.main);
            mySendMessage(chatId, "Great! your setting changed. you can click " + user.getPreferredCategory2() + " to see your news");
            user.setPreferredCategory2Country(NewsAPIClient.Country.valueOf(text.toLowerCase()));
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.settings_main && text.equalsIgnoreCase("number of news")) {
            user.setPhase(Phase.NewsAPI.setting_times);
            mySendMessage(chatId, "now you see: " + user.getNumOfNews() + " articles each time.\nsend a number of messages you want to see.");
            return;
        }
        if (phase == Phase.NewsAPI.setting_times) {
            if (!Utils.isInteger(text)) {
                mySendMessage(chatId, "this is must to be a number. try again");
                return;
            }
            user.setPhase(Phase.NewsAPI.main);
            user.setNumOfNews(Integer.parseInt(text));
            mySendMessage(chatId, "Great! now you will see: " + user.getNumOfNews() + " articles each time.");
            sendNewsOptionsToUser(user);
            return;
        }
        if (phase == Phase.NewsAPI.settings_main && text.equalsIgnoreCase("return back")) {
            user.setPhase(Phase.NewsAPI.main);
            sendNewsOptionsToUser(user);
            return;
        }
        sendMessageWithKeyboard(chatId, "sorry, it's seems like there is no function like this. what do you want to do? ", 2, "return to home", "do nothing");
    }

    public void jokesAPI(User user, int messageType, String text, Update update) {
        SendMessage sendMessage = new SendMessage();
        long chatId = user.getId();
        sendMessage.setChatId(chatId);
        Enum<?> phase = user.getPhase();
        if (text.equalsIgnoreCase("return to home")) {
            mySendMessage(chatId, " returning to home");

            sendHomeOptionsToUser(user);
            return;
        }
        if (text.equalsIgnoreCase("do nothing")) {
            mySendMessage(chatId, "sure. keep going");
            return;
        }
        if (phase == Phase.JokesAPI.first) {
            sendJokesOptionsToUser(chatId);
            user.setPhase(Phase.JokesAPI.category);
            return;
        }
        if (phase == Phase.JokesAPI.category && Objects.equals(text.toLowerCase(), "setting")) {
            sendJokeSettingOptionsToUser(user);
            return;
        }
        if (phase == Phase.JokesAPI.category && Objects.equals(text.toLowerCase(), "random joke")) {

            JokeAPIClient jokeAPIClient = new JokeAPIClient();
            JokesModel joke = jokeAPIClient.getRandomJoke();
            if (joke.getType().equals("single")) mySendMessage(chatId, joke.getJoke());
            else {
                mySendMessage(chatId, joke.getSetup());
                mySendMessage(chatId, joke.getDelivery());
            }
            sendJokesOptionsToUser(chatId);
            UserStatistics.insertActivity(new Activity(user.getId(), Query.Jokes, LocalDateTime.now()));
            return;
        }
        if (phase == Phase.JokesAPI.category && Objects.equals(text.toLowerCase(), "my preferred joke")) {

            if (user.hasNoPreferences()) {
                mySendMessage(chatId, " you have no preferences. please choose preferences first");
                sendJokeSettingOptionsToUser(user);
                return;
            }
            JokeAPIClient jokeAPIClient = new JokeAPIClient(user.getPreferredJokeCategories(), user.getUnwantedFlags(), user.getWantedFlags());
            JokesModel joke = jokeAPIClient.getSpecificJoke();
            if (joke == null || !joke.isJokeOkWithFlags()) {
                mySendMessage(chatId, "I didn't manage to find joke with your preferred settings...\n here's a random joke instead");
                joke = jokeAPIClient.getRandomJoke();
            }
            if (joke.getType().equals("single")) mySendMessage(chatId, joke.getJoke());
            else {
                mySendMessage(chatId, joke.getSetup());
                mySendMessage(chatId, joke.getDelivery());
            }
            UserStatistics.insertActivity(new Activity(user.getId(), Query.Jokes, LocalDateTime.now()));
            sendJokesOptionsToUser(chatId);
            return;
        }
        if (phase == Phase.JokesAPI.category && text.equalsIgnoreCase("return back")) {
            mySendMessage(chatId, "returning back");
            sendHomeOptionsToUser(user);
            return;
        }
        if (phase == Phase.JokesAPI.setting) {
            if (messageType == POLL_TYPE) {
                if (update.getPollAnswer().getPollId().equals(user.getCurrentUnwantedFlagsPollId())) {
                    List<JokeAPIClient.Flags> newUnwantedFlags = update.getPollAnswer().getOptionIds().stream().map(index -> JokeAPIClient.Flags.values()[index]).toList();
                    user.setTempUnwantedFlags(newUnwantedFlags);
                    return;
                }
                if (update.getPollAnswer().getPollId().equals(user.getCurrentWantedFlagsPollId())) {
                    List<JokeAPIClient.Flags> newWantedFlags = update.getPollAnswer().getOptionIds().stream().map(index -> JokeAPIClient.Flags.values()[index]).toList();
                    user.setTempWantedFlags(newWantedFlags);
                    return;
                }
                if (update.getPollAnswer().getPollId().equals(user.getCurrentCategoriesPollId())) {
                    List<JokeAPIClient.Categories> newWantedCategories = update.getPollAnswer().getOptionIds().stream().map(index -> JokeAPIClient.Categories.values()[index]).toList();
                    user.setTempPreferredJokeCategories(newWantedCategories);
                    return;
                }
                return;
            }
            if (text.equalsIgnoreCase("done")) {
                if (user.isNoPollVoted()) {
                    mySendMessage(chatId, "zero change found. your preferences saved from last time");
                    user.setPhase(Phase.JokesAPI.category);
                    sendJokesOptionsToUser(chatId);
                    return;
                }
                final boolean[] noMatch = {true};
                user.getTempUnwantedFlags().forEach(unwantedFlag -> {
                    if (user.getTempWantedFlags().contains(unwantedFlag)) {
                        noMatch[0] = false;
                    }
                });
                if (noMatch[0]) {
                    user.setUnwantedFlags(user.getTempUnwantedFlags());
                    user.setWantedFlags(user.getTempWantedFlags());
                    user.setPreferredJokeCategories(user.getTempPreferredJokeCategories());
                    mySendMessage(chatId, "great! your preferred setting saved!");
                    user.resetTempPreferences();
                    sendJokesOptionsToUser(chatId);
                    user.setPhase(Phase.JokesAPI.category);
                } else {
                    mySendMessage(chatId, "you can't choose same flag for wanted and unwanted");
                    sendJokeSettingOptionsToUser(user);
                    user.setPhase(Phase.JokesAPI.setting);
                    return;
                }
                return;
            }
            sendMessageWithKeyboard(chatId, "sorry, it's seems like there is no function like this. what do you want to do? ", 2, "return to home", "do nothing");
        }

    }

    public void countriesAPI(User user, int messageType, String text) {
        SendMessage sendMessage = new SendMessage();
        long chatId = user.getId();
        sendMessage.setChatId(chatId);
        Enum<?> phase = user.getPhase();
        if (text.equalsIgnoreCase("return to home")) {
            mySendMessage(chatId, " returning to home");
            sendHomeOptionsToUser(user);
            return;
        }
        if (text.equalsIgnoreCase("do nothing")) {
            mySendMessage(chatId, "sure. keep going");
            return;
        }
        if (phase == Phase.CountryAPI.first) {
            sendCountriesOptionsToUser(chatId);
            user.setPhase(Phase.CountryAPI.countryCode);
            return;
        }
        if (phase == Phase.CountryAPI.countryCode && Objects.equals(text.toLowerCase(), "preferred setting")) {
            sendMessageWithKeyboard(chatId, "country Api setting\ndo you want to get the country flag as Image?\n now the flag " + (user.isToSendFlag() ? "is" : "isn't") + " sent to you.", 2, "yes", "no", "return back");
            user.setPhase(Phase.CountryAPI.setting);
            return;
        }
        if (phase == Phase.CountryAPI.countryCode && Objects.equals(text.toLowerCase(), "random country")) {
            CountryAPIClient countryAPIClient = new CountryAPIClient();
            CountryModel countryModel = countryAPIClient.randomCountryAPI();
            sendCountryToUser(user, countryModel);
            return;
        }
        if (phase == Phase.CountryAPI.countryCode && Objects.equals(text.toLowerCase(), "specific country")) {
            mySendMessage(chatId, "country Api\nplease write country Code");
            return;
        }
        if (phase == Phase.CountryAPI.countryCode && Objects.equals(text.toLowerCase(), "return back")) {
            mySendMessage(chatId, "returning to home");
            sendHomeOptionsToUser(user);
            return;
        }
        if (phase == Phase.CountryAPI.setting) {
            if (Objects.equals(text, "yes")) {
                user.setToSendFlag(true);
                mySendMessage(chatId, "Great! now when you will ask for country you will get the Image :)");
            } else if (Objects.equals(text, "no")) {
                user.setToSendFlag(false);
                mySendMessage(chatId, "why not? it was so cool... \nyour setting has updated");
            }
            user.setPhase(Phase.CountryAPI.countryCode);
            sendCountriesOptionsToUser(chatId);
            return;
        }
        if (phase == Phase.CountryAPI.countryCode) {
            List<String> countries = Arrays.stream(NewsAPIClient.Country.values()).filter(a -> !a.equals(NewsAPIClient.Country.noCountry)).map(NewsAPIClient.Country::toString).toList();
            if (!countries.contains(text.toLowerCase())) {
                mySendMessage(chatId, text + " is illegal country code. the options are: " + countries);
                mySendMessage(chatId, "please write the country code of the site as shown above");
                return;
            }
            CountryAPIClient countryAPIClient = new CountryAPIClient();
            CountryModel countryModel = countryAPIClient.countryAPI(text);
            sendCountryToUser(user, countryModel);
            return;
        }
        sendMessageWithKeyboard(chatId, "sorry, it's seems like there is no function like this. what do you want to do? ", 2, "return to home", "do nothing");
    }

    public void catFactsAPI(User user) {
        Enum<?> phase = user.getPhase();
        if (phase == Phase.CatAPI.first) {
            CatAPIClient catAPIClient = new CatAPIClient();
            String fact = catAPIClient.getCatAPI();
            mySendMessage(user.getId(), fact);
            sendHomeOptionsToUser(user);
            UserStatistics.insertActivity(new Activity(user.getId(), Query.Cat_Facts, LocalDateTime.now()));
        }
    }

    public long getChatId(Update update) {
        if (update.getPollAnswer() != null) return update.getPollAnswer().getUser().getId();
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        return 0;
    }

    public String getText(Update update) {
        if (update.hasMessage() && update.getPoll() != null) return update.getMessage().getText();
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        if (update.hasMessage()) {
            return update.getMessage().getText();
        }
        return "";
    }

    public int getMessageType(Update update) {
        if (update.getPollAnswer() != null) {
            return POLL_TYPE;
        }
        if (update.hasCallbackQuery()) {
            return BUTTON_TYPE;
        }
        if (update.hasMessage()) {
            return MESSAGE_TYPE;
        }
        return OTHER_TYPE;

    }

    public void sendNewsToUser(NewsModel newsModel, long chatId) {
        if (newsModel.getArticles().size() == 0)
            mySendMessage(chatId, "sorry there is no match right now, but your query is all right");
        newsModel.getArticles().forEach(article -> {
            if (article.getContent() == null || article.getContent().equals(""))
                mySendMessage(chatId, article.getTitle() + "\n" + article.getUrl());
            else mySendMessage(chatId, article.getTitle() + "\n" + article.getContent() + article.getUrl());
        });
        UserStatistics.insertActivity(new Activity(chatId, Query.News, LocalDateTime.now()));
    }

    public List<String> getAPIsOptions() {
        return activatedQueries.stream().map(query -> {
            switch (query) {
                case Cat_Facts -> {
                    return "CAT FACTS";
                }
                case Countries -> {
                    return "COUNTRIES DATA";
                }
                case Jokes -> {
                    return "FUNNY JOKE";
                }
                case News -> {
                    return "NEWS";
                }
                case Numbers -> {
                    return "NUMBERS FACTS";
                }
                default -> {
                    return "";
                }
            }
        }).toList();
    }

    public void sendNewsSiteSettingName(long chatId) {
        sendMessageWithKeyboard(chatId, "choose site from below or just create one yourself", 1, "israel google news", "Ynet", "costume", "return back");
    }

    public void sendNewsCategoriesOptionsToUser(User user) {
        List<String> possibleCategories = Arrays.stream(NewsAPIClient.Category.values()).filter(category -> category != NewsAPIClient.Category.myCategory && !user.getUsedCategories().contains(category)).map(NewsAPIClient.Category::toString).toList();
        String pollId = mySendPoll(user.getId(), "choose your category and vote", false, possibleCategories);
        user.setCurrentNewsCategoriesPollId(pollId);
    }

    public void sendNewsSettingOptionsToUser(long chatId) {
        sendMessageWithKeyboard(chatId, "what settings do you want to change?", 2, "domain", "category1", "category2", "number of news", "return back");
    }

    public void sendNewsOptionsToUser(User user) {
        long chatId = user.getId();
        String category1 = user.getPreferredCategory1().toString();
        String category2 = user.getPreferredCategory2().toString();
        String siteName = user.getPreferredSourcedName();
        sendMessageWithKeyboard(chatId, "what do you want to do?", 2, "setting", siteName, category1, category2, "return back");
    }

    public void sendJokeSettingOptionsToUser(User user) {
        mySendMessage(user.getId(), "please vote the flags you dont want to see, the flags you want, the categories and press done when finished.");
        String pollId = mySendPoll(user.getId(), "Choose which flags you don't want to see", true, Arrays.stream(JokeAPIClient.Flags.values()).map(Enum::toString).toList());
        user.setCurrentUnwantedFlagsPollId(pollId);
        pollId = mySendPoll(user.getId(), "Choose which flags you do want to see", true, Arrays.stream(JokeAPIClient.Flags.values()).map(Enum::toString).toList());
        user.setCurrentWantedFlagsPollId(pollId);
        pollId = mySendPoll(user.getId(), "Choose which categories you do want to see", true, Arrays.stream(JokeAPIClient.Categories.values()).map(Enum::toString).toList());
        user.setCurrentCategoriesPollId(pollId);
        sendMessageWithKeyboard(user.getId(), "press done when you finished", 1, "Done");
        user.setPhase(Phase.JokesAPI.setting);

    }

    public void sendJokesOptionsToUser(Long chatId) {
        sendMessageWithKeyboard(chatId, "jokes Api\nchose what do you want to do\nor send a category", 2, "setting", "my preferred joke", "random joke", "return back");
    }

    public void sendCountriesOptionsToUser(Long chatId) {
        sendMessageWithKeyboard(chatId, "country Api\nchose what do you want to do\nor send a country Code!", 2, "preferred setting", "random country", "specific country", "return back");
    }

    public void sendHomeOptionsToUser(User user) {
        user.setPhase(Phase.Home.home);
        sendMessageWithKeyboard(user.getId(), "what do you want to do?", 1, getAPIsOptions());
    }

    public void sendCountryToUser(User user, CountryModel countryModel) {
        SendMessage sendMessage = new SendMessage();
        long chatId = user.getId();
        sendMessage.setChatId(chatId);
        if (countryModel != null && countryModel.getCapital() != null) {
            String message = "name: " + countryModel.getName() + "\ncapital: " + countryModel.getCapital() + "\nregion: " + countryModel.getRegion() + "\npopulation: " + countryModel.getPopulation() + "\narea: " + countryModel.getArea();
            if (user.isToSendFlag()) mySendPhoto(user.getId(), countryModel.getFlags().getPng());
            else message += "\ngo to setting to see the flag";
            mySendMessage(chatId, message);
        } else {
            mySendMessage(chatId, "sorry, no country found");
        }
        UserStatistics.insertActivity(new Activity(user.getId(), Query.Countries, LocalDateTime.now()));
        user.setPhase(Phase.CountryAPI.countryCode);
        sendCountriesOptionsToUser(chatId);
    }

    public void sendMessageWithKeyboard(long chatId, String header, int numOfButtonsInLine, String... buttons) {
        List<String> list = Arrays.stream(buttons).toList();
        sendMessageWithKeyboard(chatId, header, numOfButtonsInLine, list);
    }

    public void sendMessageWithKeyboard(long chatId, String header, int numOfButtonsInLine, List<String> list) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(header);
        List<List<InlineKeyboardButton>> keyBoard = IntStream.range(0, (list.size() + numOfButtonsInLine - 1) / numOfButtonsInLine).mapToObj(i -> list.subList(i * numOfButtonsInLine, Math.min((i + 1) * numOfButtonsInLine, list.size())).stream().map(buttonText -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonText);
            button.setCallbackData(buttonText);
            return button;
        }).collect(Collectors.toList())).collect(Collectors.toList());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyBoard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        send(sendMessage);
    }

    public String mySendPoll(long chatId, String question, boolean allowMultipleAnswers, List<String> answers) {
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(chatId);
        sendPoll.setQuestion(question);
        sendPoll.setOptions(answers);
        sendPoll.setIsAnonymous(false);
        sendPoll.setType("regular");
        sendPoll.setAllowMultipleAnswers(allowMultipleAnswers);

        try {
            Message message = execute(sendPoll);
            return message.getPoll().getId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void mySendMessage(long chatId, String messageString) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageString);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void mySendPhoto(long chatId, String imageUrl) {
        try {
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            File tempFile = File.createTempFile("temp", ".png");
            ImageIO.write(image, "png", tempFile);

            // Create InputFile from the temporary file
            InputFile inputFile = new InputFile(tempFile);

            // Create SendPhoto object with the chat ID and the InputFile
            SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatId), inputFile);

            execute(sendPhoto);
            tempFile.delete();
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public GraphsPanel getGraphsPanel() {
        return graphsPanel;
    }

    public void setGraphsPanel(GraphsPanel graphsPanel) {
        this.graphsPanel = graphsPanel;
    }

    public enum Query {
        Countries, News, Cat_Facts, Jokes, Numbers
    }

}
