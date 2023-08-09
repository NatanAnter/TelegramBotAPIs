package org.example.API.JokesAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class JokesModel {

    private String error;
    private String category;
    private String type;
    private String joke;
    private String setup;
    private String delivery;
    private int id;
    private String safe;
    private String lang;
    private boolean jokeOkWithFlags;
    private FlagsNSFWModel flags;

    public FlagsNSFWModel getFlags() {
        return flags;
    }

    public void setFlags(FlagsNSFWModel flags) {
        this.flags = flags;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSafe() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isJokeOkWithFlags() {
        return jokeOkWithFlags;
    }

    public void setJokeOkWithFlags(boolean jokeOkWithFlags) {
        this.jokeOkWithFlags = jokeOkWithFlags;
    }
}
