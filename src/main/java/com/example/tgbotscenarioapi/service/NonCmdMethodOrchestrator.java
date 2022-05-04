package com.example.tgbotscenarioapi.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;

@Component
@Scope("prototype")
public class NonCmdMethodOrchestrator {

    private Object currentScenario;

    private CharacterContainer characterContainer;

    protected NonCmdMethodOrchestrator prepare(Object currentScenario, CharacterContainer characterContainer){
        this.currentScenario = currentScenario;
        this.characterContainer = characterContainer;

        return this;
    }

    private final ObjectProvider<NonCmdHookContainer> nonCmdHookContainerObjectProvider;

    @Autowired
    public NonCmdMethodOrchestrator(ObjectProvider<NonCmdHookContainer> nonCmdHookContainerObjectProvider) {
        this.nonCmdHookContainerObjectProvider = nonCmdHookContainerObjectProvider;
    }

    public void registerHook(String methodName){
        try{
            Method method = currentScenario.getClass().getMethod(methodName, Update.class);

            if (characterContainer.getNonCmdHookContainer() == null){
                characterContainer.setNonCmdHookContainer(nonCmdHookContainerObjectProvider.getObject().getContainer(currentScenario, method));
            }else{
                throw new RuntimeException("Hook already registered");
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
