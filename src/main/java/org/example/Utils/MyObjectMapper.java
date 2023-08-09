package org.example.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.List;


public class MyObjectMapper<T> {
    public List<T> mapObjectList(String url, Class<T> tClass) {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (UnirestException | JsonProcessingException e) {
            System.out.println("error object Mapper:" + e);
            throw new RuntimeException(e);
        }
    }

    public T mapObject(String url, Class<T> tClass) {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), tClass);
        } catch (UnirestException | JsonProcessingException e) {
            System.out.println("error object Mapper: " + e);
            throw new RuntimeException(e);
        }

    }
}
