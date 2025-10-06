package com.railway.booking.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class ApiClient {
    private static final String API_BASE = "http://localhost:8080/api";
    private final OkHttpClient http = new OkHttpClient();
    private final Gson gson = new Gson();

    public String get(String endpoint) throws IOException {
        Request req = new Request.Builder().url(API_BASE + endpoint).get().build();
        try (Response res = http.newCall(req).execute()) {
            if (!res.isSuccessful()) throw new IOException("Unexpected code " + res);
            return res.body().string();
        }
    }

    public String post(String endpoint, String body) throws IOException {
        RequestBody reqBody = RequestBody.create(body, MediaType.parse("application/json"));
        Request req = new Request.Builder().url(API_BASE + endpoint).post(reqBody).build();
        try (Response res = http.newCall(req).execute()) {
            if (!res.isSuccessful()) throw new IOException("Unexpected code " + res);
            return res.body().string();
        }
    }

    public JsonObject post(String endpoint, Map<String, String> data) throws IOException {
        String jsonResponse = post(endpoint, gson.toJson(data));
        return gson.fromJson(jsonResponse, JsonObject.class);
    }
    
    public JsonObject post(String endpoint, Object data) throws IOException {
        String jsonResponse = post(endpoint, gson.toJson(data));
        return gson.fromJson(jsonResponse, JsonObject.class);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }
}
