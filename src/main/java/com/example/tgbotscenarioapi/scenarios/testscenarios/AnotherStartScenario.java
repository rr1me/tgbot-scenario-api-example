package com.example.tgbotscenarioapi.scenarios.testscenarios;

import com.example.tgbotscenarioapi.annotations.MethodParam;
import com.example.tgbotscenarioapi.annotations.ScenarioController;
import com.example.tgbotscenarioapi.annotations.ScenarioRequest;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@ScenarioController(parallel = true)
public class AnotherStartScenario {

    @ScenarioRequest(value = "/do", param = MethodParam.START)
    public BotApiMethod<?> run(Update update){

        return new SendMessage(String.valueOf(update.getMessage().getChatId()), "?;");
    }

    @ScenarioRequest(value = "/domake", param = MethodParam.ONGOING)
    public BotApiMethod<?> domake(Update update){
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), "make smth");
    }

    @ScenarioRequest(value = "/quit", param = MethodParam.EXIT)
    public BotApiMethod<?> quit(Update update){

        return new SendMessage(String.valueOf(update.getMessage().getChatId()), "quit");
    }
}
