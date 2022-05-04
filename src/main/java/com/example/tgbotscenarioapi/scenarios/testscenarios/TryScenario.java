package com.example.tgbotscenarioapi.scenarios.testscenarios;

import com.example.tgbotscenarioapi.annotations.MethodParam;
import com.example.tgbotscenarioapi.annotations.ScenarioController;
import com.example.tgbotscenarioapi.annotations.ScenarioRequest;
import com.example.tgbotscenarioapi.service.NonCmdMethodOrchestrator;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@ScenarioController
public class TryScenario {

    @ScenarioRequest(value = "/try", param = MethodParam.HYBRID)
    public BotApiMethod<?> trytest(Update update, NonCmdMethodOrchestrator nonCmdMethodOrchestrator){
        nonCmdMethodOrchestrator.registerHook("tryhook");
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), "try");
    }

    public BotApiMethod<?> tryhook(Update update){
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), "tryhook?");
    }
}
