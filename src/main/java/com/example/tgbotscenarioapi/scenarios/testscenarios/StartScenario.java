package com.example.tgbotscenarioapi.scenarios.testscenarios;

import com.example.tgbotscenarioapi.annotations.MethodParam;
import com.example.tgbotscenarioapi.annotations.ScenarioController;
import com.example.tgbotscenarioapi.annotations.ScenarioRequest;
import com.example.tgbotscenarioapi.service.NonCmdMethodOrchestrator;
import com.example.tgbotscenarioapi.util.SendMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@ScenarioController(parallel = true)
public class StartScenario {

    private final SendMsg sendMsg;

    @Autowired
    public StartScenario(SendMsg sendMsg) {
        this.sendMsg = sendMsg;
    }

    @ScenarioRequest(value = "/start", param = MethodParam.START)
    public BotApiMethod<?> start(Update update){
        return sendMsg.s(update, "starting, chat id: "+update.getMessage().getChatId());
    }

    @ScenarioRequest(value = "testhook", param = MethodParam.ONGOING)
    public BotApiMethod<?> structureQuestion(Update update, NonCmdMethodOrchestrator nonCmdMethodOrchestrator){
        nonCmdMethodOrchestrator.registerHook("hook");

        return sendMsg.s(update, "registered");
    }

    public BotApiMethod<?> hook(Update update){
        return sendMsg.s(update, "hook works");
    }

    @ScenarioRequest(value = "/exit", param = MethodParam.EXIT)
    public BotApiMethod<?> exit(Update update) {
        return sendMsg.s(update, "Exit from start scenario");
    }
}
