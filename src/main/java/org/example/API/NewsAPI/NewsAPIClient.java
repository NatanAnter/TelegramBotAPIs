package org.example.API.NewsAPI;

import org.example.Utils.MyObjectMapper;

import java.util.Objects;

public class NewsAPIClient {
    public static final int TIMES_DEFAULT = 5;
    public static final String MY_API_KEY = "8868f50753c341079e6d1c669dbebbe2";
    private String request;
    private String language;
    private String country;
    private String searchInTitle;
    private String search;
    private String fromDate;
    private String toDate;
    private String sortBy;
    private String category;
    private String source;
    private int times;

    public NewsAPIClient() {
    }

    public NewsAPIClient(String request, String language, String country, String searchInTitle, String search, String fromDate, String toDate, String sortBy, String category, String source, int times) {
        this.request = request;
        this.language = language;
        this.country = country;
        this.searchInTitle = searchInTitle;
        this.search = search;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.sortBy = sortBy;
        this.category = category;
        this.source = source;
        this.times = times;
    }

    public NewsModel getAllSources() {
        return new MyObjectMapper<NewsModel>().mapObject("https://newsapi.org/v2/top-headlines/sources?apiKey=" + MY_API_KEY, NewsModel.class);
    }

    public NewsModel searchBySourceAPI(String source, int times) {
        return newsAPI("everything", "", "", "", "", "", "", "publishedAt", "", "", source, times);
    }

    public NewsModel searchByDomainAndCountryAPI(Country country, String domains, int times) {
        return newsAPI("everything", "", country.toString(), "", "", "", "", "publishedAt", "", "", domains, times);
    }

    public NewsModel searchByCountryAndCategoryAPI(Country country, Category category, int times) {
        return newsAPI("top-headlines", "", country.toString(), "", "", "", "", "publishedAt", category.toString(), "", "", times);
    }

    public NewsModel searchTitleNewsAPI(String request, String language, String searchInTitle, String fromDate, String toDate, String sortBy, String category, int times) {
        return newsAPI(request, language, "", searchInTitle, "", fromDate, toDate, sortBy, category, "", "", times);
    }

    public NewsModel searchNewsAPI(String request, String language, String search, String fromDate, String toDate, String sortBy, String category, int times) {
        return newsAPI(request, language, "", "", search, fromDate, toDate, sortBy, category, "", "", times);
    }

    public NewsModel newsAPI(String request, String language, String country, String searchInTitle, String search, String fromDate, String toDate, String sortBy, String category, String domains, String sources, int times) {
        MyObjectMapper<NewsModel> m = new MyObjectMapper();
        String query = "https://newsapi.org/v2/";
        if (Objects.equals(request, "top-headlines")) query += "top-headlines?";
        else query += "everything?";
        if (!Objects.equals(language, "")) query += "language=" + language + "&";
        if (!Objects.equals(searchInTitle, "")) query += "q=" + searchInTitle + "&";
        if (!Objects.equals(search, "")) query += "search=" + search + "&";
        if (!Objects.equals(country, "")) query += "country=" + country + "&";
        if (!Objects.equals(fromDate, "")) query += "from=" + fromDate + "&";
        if (!Objects.equals(toDate, "")) query += "to=" + toDate + "&";
        if (!Objects.equals(category, "")) query += "category=" + category + "&";
        if (!Objects.equals(sortBy, "")) query += "sortBy=" + sortBy + "&";
        if (!Objects.equals(domains, "")) query += "domains=" + domains + "&";
        if (!Objects.equals(sources, "")) query += "sources=" + sources + "&";
        query += "pageSize=" + times + "&page=1&";
        query += "apiKey=" + MY_API_KEY;

        NewsModel newsModel = m.mapObject(query, NewsModel.class);
        if (newsModel.getStatus() == null) {
            System.out.println("error: query didnt worked");
            return null;
        }
        return newsModel;
    }

    public enum Category {
        myCategory, business, entertainment, general, health, science, sports, technology;

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public enum Country {
        noCountry, at, au, be, bg, br, ca, ch, cn, co, cu, cz, de, eg, fr, gb, gr, hk, hu, id, ie, il, in, it, jp, kr, lt, lv, ma, mx, my, ng, nl, no, nz, ph, pl, pt, ro, rs, ru, sa, se, sg, si, sk, th, tr, tw, ua, us, ve, za;

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
