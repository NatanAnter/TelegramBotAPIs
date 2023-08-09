package org.example.API.JokesAPI;

import org.example.Utils.MyObjectMapper;

import java.util.List;
import java.util.Objects;

public class JokeAPIClient {
    public static final int MAX_TRIES_FRO_JOKE = 20;
    private List<Categories> categories;
    private List<Flags> flagsToRemove;
    private List<Flags> flagsToAdd;

    public JokeAPIClient(List<Categories> categories, List<Flags> flagsToRemove, List<Flags> flagsToAdd) {
        this.categories = categories;
        this.flagsToRemove = flagsToRemove;
        this.flagsToAdd = flagsToAdd;
    }

    public JokeAPIClient() {
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }

    public List<Flags> getFlagsToRemove() {
        return flagsToRemove;
    }

    public void setFlagsToRemove(List<Flags> flagsToRemove) {
        this.flagsToRemove = flagsToRemove;
    }

    public List<Flags> getFlagsToAdd() {
        return flagsToAdd;
    }

    public void setFlagsToAdd(List<Flags> flagsToAdd) {
        this.flagsToAdd = flagsToAdd;
    }
    public JokesModel getRandomJoke(){
        JokesModel jokesModel = new JokesModel();
        MyObjectMapper<JokesModel> m = new MyObjectMapper<>();
        jokesModel = m.mapObject("https://v2.jokeapi.dev/joke/Any", JokesModel.class);
        return jokesModel;
    }

    public JokesModel getSpecificJoke() {
        boolean containsAllFlags;
        int times = 0;
        JokesModel jokesModel;
        do {
            MyObjectMapper<JokesModel> m = new MyObjectMapper<>();
            String url = "https://v2.jokeapi.dev/joke/";
            if (this.categories.contains(Categories.Any)||this.categories.isEmpty())
                url += "Any";
            else {
                for (Categories c : categories) {
                    url += c.toString() + ",";
                }
                url = url.substring(0, url.length() - 1);
            }
            if (this.flagsToRemove.size() != 0) {
                url += "?blacklistFlags=";
                for (Flags flag : flagsToRemove) {
                    url += flag.toString() + ",";
                }
                url = url.substring(0, url.length() - 1);

            }
            System.out.println(url);
            jokesModel = m.mapObject(url, JokesModel.class);
            if (jokesModel.getError() == null || Objects.equals(jokesModel.getError(), "False")) {
                //error
                return null;
            }
            containsAllFlags = true;
            if (!jokesModel.getFlags().isNsfw() && flagsToAdd.contains(Flags.nsfw)) {
                containsAllFlags = false;
                continue;
            }
            if (!jokesModel.getFlags().isReligious() && flagsToAdd.contains(Flags.religious)) {
                containsAllFlags = false;
                continue;
            }
            if (!jokesModel.getFlags().isPolitical() && flagsToAdd.contains(Flags.political)) {
                containsAllFlags = false;
                continue;
            }
            if (!jokesModel.getFlags().isRacist() && flagsToAdd.contains(Flags.racist)) {
                containsAllFlags = false;
                continue;
            }
            if (!jokesModel.getFlags().isSexist() && flagsToAdd.contains(Flags.sexist)) {
                containsAllFlags = false;
                continue;
            }
            if (!jokesModel.getFlags().isExplicit() && flagsToAdd.contains(Flags.explicit)) {
                containsAllFlags = false;
                continue;
            }
        } while (!containsAllFlags && times++ < MAX_TRIES_FRO_JOKE);
        jokesModel.setJokeOkWithFlags(containsAllFlags);
        return jokesModel;
    }

    public enum Categories {
        Any,
        Programming,
        Misc,
        Dark,
        Pun,
        Spooky,
        Christmas;
        @Override
        public String toString() {
            return super.toString();
        }
    }

    public enum Flags {
        nsfw,
        religious,
        political,
        racist,
        sexist,
        explicit;

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
