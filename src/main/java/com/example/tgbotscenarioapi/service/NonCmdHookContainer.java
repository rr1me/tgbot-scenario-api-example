package com.example.tgbotscenarioapi.service;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Scope("prototype")
@Getter
public class NonCmdHookContainer {

    private Object currentScenario;

    private Method method;

    protected NonCmdHookContainer getContainer(Object currentScenario, Method method){
        this.currentScenario = currentScenario;
        this.method = method;

        return this;
    }
}
