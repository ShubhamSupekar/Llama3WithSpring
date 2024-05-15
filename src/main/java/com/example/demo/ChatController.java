package com.example.demo;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    private final MyClient client;

    public ChatController(MyClient client) {
        this.client = client;
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        return "ChatPage";
    }

    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat() {
        List<Message> customMessages = new ArrayList<>();
        customMessages.add(new Message("system", "Always answer like you are teaching"));
        customMessages.add(new Message("user", "What is the force and its formula"));

        // Call sendRequest method of MyClient
        return client.sendRequest(customMessages)
                // Filter out null values
                .filter(message -> message != null)
                // Map messages to strings using lambda expression
                .map(message -> message.toString());
    }
}