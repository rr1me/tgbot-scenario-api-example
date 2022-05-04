package com.example.tgbotscenarioapi.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class ArgScan {

    public List<String> s(Update update){
        Scanner scanner = new Scanner(update.getMessage().getText());
        List<String> scannerList = new ArrayList<>();
        while(scanner.hasNext()){
            scannerList.add(scanner.next());
        }
        return scannerList;
    }
}
