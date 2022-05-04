package com.example.tgbotscenarioapi.service;

import com.example.tgbotscenarioapi.exceptions.CharacterMistakesException;
import com.example.tgbotscenarioapi.util.SendMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Service
public class Bot extends SpringWebhookBot {

    private static final String botPath = "";

    private final MessageHandler messageHandler;
    private final SendMsg sendMsg;

    @Autowired
    public Bot(MessageHandler messageHandler, SendMsg sendMsg) {
        super(SetWebhook.builder().url(botPath).build());
        this.messageHandler = messageHandler;
        this.sendMsg = sendMsg;
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            return messageHandler.run(update);
        }catch (CharacterMistakesException e){
            return sendMsg.s(update, e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
