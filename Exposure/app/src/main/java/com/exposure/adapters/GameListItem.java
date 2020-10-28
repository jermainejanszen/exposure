package com.exposure.adapters;

import java.util.Map;

public class GameListItem {

    private final Map<String, String> gameInputs;
    private final String sender;

    public GameListItem(Map<String, String> gameInputs, String sender) {
        this.gameInputs = gameInputs;
        this.sender = sender;
    }

    public Map<String, String> getGameInputs() {
        return gameInputs;
    }

    public String getSender() {
        return sender;
    }
}
