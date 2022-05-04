package com.example.tgbotscenarioapi.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
@Scope("prototype")
@ToString
public class CharacterContainer {

    private boolean parallel;

    private Map<Object, Object> activeScenariosMap = new HashMap<>();

    private NonCmdHookContainer nonCmdHookContainer;
}
