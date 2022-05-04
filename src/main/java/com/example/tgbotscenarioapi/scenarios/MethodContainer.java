package com.example.tgbotscenarioapi.scenarios;

import com.example.tgbotscenarioapi.annotations.MethodParam;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Scope("prototype")
@Getter
public class MethodContainer {

    private Method method;

    private Class<?>[] methodArgs;

    private MethodParam methodParam;

    public MethodContainer fill(Method method, Class<?>[] methodArgs, MethodParam methodParam) {
        this.method = method;
        this.methodArgs = methodArgs;
        this.methodParam = methodParam;

        return this;
    }
}
