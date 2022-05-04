package com.example.tgbotscenarioapi.scenarios;

import com.example.tgbotscenarioapi.annotations.ScenarioController;
import com.example.tgbotscenarioapi.annotations.ScenarioRequest;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScenariosInitializingBean implements InitializingBean {

    private final ApplicationContext context;

    @Autowired
    public ScenariosInitializingBean(ApplicationContext context) {
        this.context = context;
    }

    @Getter
    private final Map<String, ScenarioCreator> scenarioCreatorMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        for (Map.Entry<String, Object> annotatedBean : context.getBeansWithAnnotation(ScenarioController.class).entrySet()){
            List<Method> methods = Arrays.stream(annotatedBean.getValue().getClass().getMethods()).filter(method -> method.isAnnotationPresent(ScenarioRequest.class)).toList();

            ScenarioCreator scenarioCreator = context.getBean(ScenarioCreator.class);
            ObjectProvider<?> scenarioProvider = context.getBeanProvider(annotatedBean.getValue().getClass());

            Map<String, MethodContainer> methodContainerMap;

            methodContainerMap = methods.stream().collect(Collectors.toMap(
                    method -> method.getAnnotation(ScenarioRequest.class).value(),
                    i -> context.getBean(MethodContainer.class).fill(i, i.getParameterTypes(), i.getAnnotation(ScenarioRequest.class).param())
                    ));

            scenarioCreatorMap.putAll(methods.stream()
                    .collect(Collectors.toMap(
                            this::getValue,
                            i -> scenarioCreator.prepare(scenarioProvider, methodContainerMap, annotatedBean.getValue().getClass().getAnnotation(ScenarioController.class).parallel(), annotatedBean.getValue())
                    )));
        }
    }

    private String getValue(Method method){
        String value = method.getAnnotation(ScenarioRequest.class).value();

        if (scenarioCreatorMap.containsKey(value))
            throw new IllegalStateException("There's already method with same command {"+value+"}");

        return value;
    }
}
