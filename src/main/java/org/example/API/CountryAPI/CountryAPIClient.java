package org.example.API.CountryAPI;

import org.example.Utils.MyObjectMapper;

import java.util.List;
import java.util.Random;

public class CountryAPIClient {
    public CountryModel randomCountryAPI() {
        Random random = new Random();
        String url = "https://restcountries.com/v2/all/";
        MyObjectMapper<CountryModel> m = new MyObjectMapper<>();
        List<CountryModel> countryModelList = m.mapObjectList(url, CountryModel.class);
        return countryModelList.get(random.nextInt(0, countryModelList.size()));
    }

    public CountryModel countryAPI(String countryCode) {
        String url = "https://restcountries.com/v2/alpha/" + countryCode;
        MyObjectMapper<CountryModel> m = new MyObjectMapper<>();
        CountryModel countryModel = m.mapObject(url, CountryModel.class);
        return countryModel;

    }
}
