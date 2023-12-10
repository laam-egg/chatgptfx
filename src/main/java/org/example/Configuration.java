package org.example;

class Configuration {
    private String chatGPTAPIKey;

    public Configuration(String chatGPTAPIKey) {
        this.chatGPTAPIKey = chatGPTAPIKey;
    }

    public Configuration() {
        chatGPTAPIKey = "";
    }

    public String getChatGPTAPIKey() {
        return chatGPTAPIKey;
    }

    public void setChatGPTAPIKey(String chatGPTAPIKey) {
        this.chatGPTAPIKey = chatGPTAPIKey;
    }
}
