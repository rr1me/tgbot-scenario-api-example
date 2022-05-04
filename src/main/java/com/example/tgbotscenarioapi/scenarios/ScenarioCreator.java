package com.example.tgbotscenarioapi.scenarios;

import lombok.Getter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope("prototype")
@Getter
public class ScenarioCreator {

    private Object scenarioInstance;

    private ObjectProvider<?> scenarioProvider;

    private Map<String, MethodContainer> methodContainerMap;

    private boolean parallel;

    public ScenarioCreator prepare(ObjectProvider<?> scenarioProvider, Map<String, MethodContainer> methodContainerMap, boolean parallel, Object scenarioInstance) {
        this.scenarioProvider = scenarioProvider;
        this.methodContainerMap = methodContainerMap;
        this.parallel = parallel;
        this.scenarioInstance = scenarioInstance;

        return this;
    }

    public Object getScenario(){

        return scenarioProvider.getObject();
    }
}
