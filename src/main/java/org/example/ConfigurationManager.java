package org.example;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ConfigurationManager {
    public static final String CONFIGURATION_FILE_NAME = "config.json";

    private final Gson gson;

    private Configuration configuration;

    public ConfigurationManager() {
        gson = new Gson();
        reload();
    }

    public void reload() {
        reloadWithRetryTimes(1);
    }

    private void reloadWithRetryTimes(int retryTimes) {
        try {
            configuration = gson.fromJson(
                    FileUtils.readFileToString(
                            new File(CONFIGURATION_FILE_NAME),
                            StandardCharsets.UTF_8
                    ),
                    Configuration.class
            );
        } catch (Exception e) {
            if (retryTimes <= 0) {
                App.exitWithFatalError(e);
            }
            reset();
            reloadWithRetryTimes(retryTimes - 1);
        }
    }

    private void reset() {
        saveConfiguration(gson, new Configuration());
    }

    private void save() {
        saveConfiguration(gson, configuration);
    }

    private static void saveConfiguration(Gson gson, Configuration configuration) {
        try {
            FileUtils.writeStringToFile(
                    new File(CONFIGURATION_FILE_NAME),
                    gson.toJson(configuration, Configuration.class),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            App.exitWithFatalError(e);
        }
    }

    public String getChatGPTAPIKey() {
        return configuration.getChatGPTAPIKey();
    }

    public void setChatGPTAPIKey(String chatGPTAPIKey) {
        configuration.setChatGPTAPIKey(chatGPTAPIKey);
        save();
    }
}
