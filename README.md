## hi

This is scenario api example for telegram webhook bot

___

## the idea:

* Use spring-controller-like structure to handle commands
* Make all commands to exist in particular scenarios without access to another scenarios if they are not parallel
* Flexibility as much as possible

___

## how it works:

It's using application context to find classes which was annotated with @ScenarioController, then searching for methods with @ScenarioRequest and saving everything in map.

When user types some command in bot chat algorithm searching for right scenarioCreator with proper scenarioController which contains start scenario request, after that it creates new scenario and saving it inside already created user container. Further, it's using reflection api to invoke method.

If user wants to start another scenario, both scenarios have to be parallel - @ScenarioController(parallel = true), otherwise he is able to use hybrid request - @ScenarioRequest(value = "/hybrid", param = MethodParam.HYBRID) or exit from his scenario by using exit request - @ScenarioRequest(value = "/exit", param = MethodParam.EXIT). Also, hybrid requests are available everytime from all controller classes no matter if user already started scenario or not.

You need to add "Update" argument in all request methods and return BotApiMethod or null.

"Middle" methods are available after start scenario by using MethodParam.ONGOING.

Simple example of default scenario:

```java
@ScenarioController
public class TestScenario {

    @ScenarioRequest(value = "/start", param = MethodParam.START)
    public BotApiMethod<?> startRequest(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        return new SendMessage(chatId, "starting, chat id: " + chatId);
    }

    @ScenarioRequest(value = "/ongoing", param = MethodParam.ONGOING)
    public BotApiMethod<?> ongoingRequest(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        return new SendMessage(chatId, "ongoing request");
    }

    @ScenarioRequest(value = "/exit", param = MethodParam.EXIT)
    public BotApiMethod<?> exitRequest(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        return new SendMessage(chatId, "exit from scenario");
    }
}
```

If you want to intercept user's message with no matter if its contains any command, you can register nonCmdHook. To do that, you need to add optional argument called NonCmdMethodOrchestrator and use registerHook method with method name of your hook. 

```java
@ScenarioRequest(value = "/hook", param = MethodParam.HYBRID)
public BotApiMethod<?> trytest(Update update, NonCmdMethodOrchestrator nonCmdMethodOrchestrator){
    nonCmdMethodOrchestrator.registerHook("hook");

    String chatId = String.valueOf(update.getMessage().getChatId());
    return new SendMessage(chatId, "hook registered");
}

public BotApiMethod<?> hook(Update update){
    String chatId = String.valueOf(update.getMessage().getChatId());
    return new SendMessage(chatId, "hook?");
}
```

Registered hook saves in user's container, after invoking method hook deletes. Only one hook available to register at one time

___

## why i did this:

1. I think the actual concept of spring-controller-like command handler is interesting idea to use in bots. Maybe this will help someone.
2. Need to learn stuff, heh
3. why not?

___

```text
MIT License

Copyright (c) 2022 rr1me

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```