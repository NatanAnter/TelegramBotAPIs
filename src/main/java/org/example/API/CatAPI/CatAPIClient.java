package org.example.API.CatAPI;

import org.example.Utils.MyObjectMapper;

public class CatAPIClient {

    public String getCatAPI() {
        MyObjectMapper<CatModel> m = new MyObjectMapper();
        CatModel catModel = m.mapObject("https://catfact.ninja/fact", CatModel.class);
        if (catModel.getFact() == null) {
            return "error";
        }
        return catModel.getFact();
    }
}
