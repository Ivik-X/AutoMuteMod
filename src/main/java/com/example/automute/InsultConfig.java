
package com.example.automute;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class InsultConfig {
    private static final Path CONFIG_PATH = Paths.get("config/automute_words.json");
    private List<String> simpleInsults = new ArrayList<>();
    private List<String> familyInsults = new ArrayList<>();

    public InsultConfig() {
        load();
    }

    public void load() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                save();
                return;
            }

            JsonObject json = new Gson().fromJson(Files.newBufferedReader(CONFIG_PATH), JsonObject.class);
            simpleInsults = new Gson().fromJson(json.get("simple"), new TypeToken<List<String>>(){}.getType());
            familyInsults = new Gson().fromJson(json.get("family"), new TypeToken<List<String>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        JsonObject json = new JsonObject();
        json.add("simple", new Gson().toJsonTree(simpleInsults));
        json.add("family", new Gson().toJsonTree(familyInsults));

        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            new Gson().toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSimpleInsults() { return simpleInsults; }
    public List<String> getFamilyInsults() { return familyInsults; }
}