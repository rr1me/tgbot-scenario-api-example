package com.example.tgbotscenarioapi.service;

import com.example.tgbotscenarioapi.annotations.MethodParam;
import com.example.tgbotscenarioapi.exceptions.CharacterMistakesException;
import com.example.tgbotscenarioapi.exceptions.NoSuchScenarioExcp;
import com.example.tgbotscenarioapi.scenarios.MethodContainer;
import com.example.tgbotscenarioapi.scenarios.ScenarioCreator;
import com.example.tgbotscenarioapi.scenarios.ScenariosInitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageHandler {

    private final ScenariosInitializingBean scenariosInitializingBean;
    private final ObjectProvider<CharacterContainer> characterContainerObjectProvider;
    private final ObjectProvider<NonCmdMethodOrchestrator> nonCmdMethodOrchestratorObjectProvider;

    @Autowired
    public MessageHandler(ScenariosInitializingBean scenariosInitializingBean, ObjectProvider<CharacterContainer> characterContainerObjectProvider, ObjectProvider<NonCmdMethodOrchestrator> nonCmdMethodOrchestratorObjectProvider) {
        this.scenariosInitializingBean = scenariosInitializingBean;
        this.characterContainerObjectProvider = characterContainerObjectProvider;
        this.nonCmdMethodOrchestratorObjectProvider = nonCmdMethodOrchestratorObjectProvider;
    }

    private final Map<Long, CharacterContainer> characterContainerMap = new HashMap<>();

    public BotApiMethod<?> run(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            CharacterContainer characterContainer = getContainer(update);

            if (characterContainer.getNonCmdHookContainer() == null){
                return cmdHandler(update, characterContainer);
            }else{
                return nonCmdHandler(update, characterContainer);
            }
        }
        return null;
    }

    private CharacterContainer getContainer(Update update){
        long chatId = update.getMessage().getChatId();
        CharacterContainer characterContainer;

        if (!characterContainerMap.containsKey(chatId)) {
            characterContainer = characterContainerObjectProvider.getObject();
            characterContainerMap.put(chatId, characterContainer);
        } else {
            characterContainer = characterContainerMap.get(chatId);
        }
        return characterContainer;
    }

    private BotApiMethod<?> nonCmdHandler(Update update, CharacterContainer characterContainer){
        NonCmdHookContainer nonCmdHookContainer = characterContainer.getNonCmdHookContainer();

        if (nonCmdHookContainer != null){
            characterContainer.setNonCmdHookContainer(null);
            return invokeMethod(nonCmdHookContainer.getMethod(), nonCmdHookContainer.getCurrentScenario(), update, characterContainer);
        }

        return null;
    }

    private BotApiMethod<?> cmdHandler(Update update, CharacterContainer characterContainer) {
        String requestedScenario = update.getMessage().getText().split(" ")[0].trim();
        Map<String, ScenarioCreator> scenarioCreatorMap = scenariosInitializingBean.getScenarioCreatorMap();

        if (!scenarioCreatorMap.containsKey(requestedScenario))
            throw new NoSuchScenarioExcp();

        ScenarioCreator scenarioCreator = scenarioCreatorMap.get(requestedScenario);

        Map<String, MethodContainer> methodContainerMap = scenarioCreator.getMethodContainerMap();

        Map<Object, Object> characterActiveScenariosMap = characterContainer.getActiveScenariosMap();

        boolean isRequestedScenarioParallel = scenarioCreator.isParallel();
        Object currentScenario;

        MethodContainer methodContainer = methodContainerMap.get(requestedScenario);
        MethodParam methodParam = methodContainer.getMethodParam();

        if (characterActiveScenariosMap.isEmpty()) {
            if (methodParam == MethodParam.EXIT || methodParam == MethodParam.ONGOING)
                throw new CharacterMistakesException("try /help?");

            currentScenario = scenarioCreator.getScenario();
        } else {
            if (!characterActiveScenariosMap.containsKey(scenarioCreator.getScenarioInstance())) {
                if (methodParam == MethodParam.START) {
                    if (isRequestedScenarioParallel && characterContainer.isParallel()) {
                        currentScenario = scenarioCreator.getScenarioInstance();
                    } else
                        throw new CharacterMistakesException("Scenarios isn't parallel");
                } else if (methodParam == MethodParam.HYBRID) {
                    currentScenario = scenarioCreator.getScenario();
                } else
                    throw new CharacterMistakesException("This method from other scenario and its not hybrid or start");
            } else {
                if (methodParam == MethodParam.START)
                    throw new CharacterMistakesException("You're trying to start same scenario");

                currentScenario = characterActiveScenariosMap.get(scenarioCreator.getScenarioInstance());
            }
        }
        return selectAndInvoke(methodContainer, methodParam, characterActiveScenariosMap, characterContainer, scenarioCreator, currentScenario, isRequestedScenarioParallel, update);
    }

    private BotApiMethod<?> selectAndInvoke(MethodContainer methodContainer, MethodParam methodParam, Map<Object, Object> characterActiveScenariosMap, CharacterContainer characterContainer,
                                            ScenarioCreator scenarioCreator, Object currentScenario, boolean isRequestedScenarioParallel, Update update){

        Method method = methodContainer.getMethod();
        BotApiMethod<?> response = null;
        switch (methodParam) {
            case START -> {
                response =  invokeMethod(method, currentScenario, update, characterContainer);

                characterActiveScenariosMap.put(scenarioCreator.getScenarioInstance(), currentScenario);
                characterContainer.setParallel(isRequestedScenarioParallel);
            }
            case ONGOING, HYBRID -> response = invokeMethod(method, currentScenario, update, characterContainer);
            case EXIT -> {

                response = invokeMethod(method, currentScenario, update, characterContainer);

                characterActiveScenariosMap.remove(scenarioCreator.getScenarioInstance());
            }
        }
        return response;
    }

    private BotApiMethod<?> invokeMethod(Method method, Object currentScenario, Update update, CharacterContainer characterContainer) {
        try {
            if (method.getParameterCount() > 1)
                return (BotApiMethod<?>) method.invoke(currentScenario, update, nonCmdMethodOrchestratorObjectProvider.getObject().prepare(currentScenario, characterContainer));
            else
                return (BotApiMethod<?>) method.invoke(currentScenario, update);

        } catch (IllegalAccessException | InvocationTargetException e){
            if (e.getCause().toString().contains("CharacterMistakesException")){
                throw new CharacterMistakesException(e.getCause().toString().split(" ")[1].trim());
            }
            e.printStackTrace();
            return null;
        }
    }

}
