package com.example.tgbotscenarioapi.exceptions;

public class NoSuchScenarioExcp extends CharacterMistakesException{
    public NoSuchScenarioExcp() {
        super("There's no such scenario, try /start");
    }
}
