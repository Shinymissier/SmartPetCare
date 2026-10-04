package com.petcare.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SupabaseRestClient {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Gson GSON = new Gson();

    /**
     * Test connection to Supabase REST API
     */
    public static boolean testConnection() {
        AppConfig config = AppConfig.getInstance();
        if (!config.isSupabaseConfigured()) {
            return false;
        }

        try {
            String url = config.getSupabaseUrl().replaceAll("/+$", "") + "/rest/v1/";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", config.getSupabaseAnonKey())
                    .header("Authorization", "Bearer " + config.getSupabaseAnonKey())
                    .GET()
                    .timeout(Duration.ofSeconds(6))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 400;
        } catch (Exception e) {
            System.err.println("Supabase REST test connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Query data from Supabase REST API (PostgREST table endpoint)
     */
    public static JsonArray queryTable(String table, String queryParams) {
        AppConfig config = AppConfig.getInstance();
        if (!config.isSupabaseConfigured()) {
            return new JsonArray();
        }

        try {
            String url = config.getSupabaseUrl().replaceAll("/+$", "") + "/rest/v1/" + table;
            if (queryParams != null && !queryParams.isEmpty()) {
                url += "?" + queryParams;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", config.getSupabaseAnonKey())
                    .header("Authorization", "Bearer " + config.getSupabaseAnonKey())
                    .header("Accept", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return JsonParser.parseString(response.body()).getAsJsonArray();
            } else {
                System.err.println("Supabase query error: " + response.statusCode() + " -> " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Exception querying Supabase table " + table + ": " + e.getMessage());
        }
        return new JsonArray();
    }

    /**
     * Insert records into Supabase REST API
     */
    public static boolean insertRecord(String table, JsonObject data) {
        AppConfig config = AppConfig.getInstance();
        if (!config.isSupabaseConfigured()) {
            return false;
        }

        try {
            String url = config.getSupabaseUrl().replaceAll("/+$", "") + "/rest/v1/" + table;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", config.getSupabaseAnonKey())
                    .header("Authorization", "Bearer " + config.getSupabaseAnonKey())
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(data)))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            System.err.println("Exception inserting record to Supabase table " + table + ": " + e.getMessage());
            return false;
        }
    }
}
