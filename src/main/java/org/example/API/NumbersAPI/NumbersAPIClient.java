package org.example.API.NumbersAPI;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.example.API.CatAPI.CatModel;
import org.example.Utils.MyObjectMapper;

public class NumbersAPIClient {
    private int number;
    private boolean isRandom;

    public NumbersAPIClient(int number, boolean isRandom) {
        this.number = number;
        this.isRandom = isRandom;
    }

    public NumbersAPIClient() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public String getRandomNumberFact() {
        isRandom = true;
        return NumberAPi();
    }

    public String getNumberFact(int x) {
        this.isRandom = false;
        this.number = x;
        return NumberAPi();
    }

    public String NumberAPi() {
        HttpResponse<String> response;
        try {
            String url = "http://numbersapi.com/";
            if (isRandom)
                url += "random";
            else
                url += number;
            response = Unirest.get(url).asString();
            return response.getBody();
        } catch (Exception e) {
            System.out.println("error numbers");
        }
        return "";
    }
}
