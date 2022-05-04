package com.example.tgbotscenarioapi.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SendMsg {

    public SendMessage s(Update update, StringBuilder builder) { return new SendMessage(String.valueOf(update.getMessage().getChatId()), builder.toString()); }

    public SendMessage s(Update update, String string) { return new SendMessage(String.valueOf(update.getMessage().getChatId()), string); }

    public SendMessage s(Long chatId, StringBuilder builder) { return new SendMessage(String.valueOf(chatId), builder.toString()); }

    public SendMessage s(Long chatId, String string) {
        return new SendMessage(String.valueOf(chatId), string);
    }
}
