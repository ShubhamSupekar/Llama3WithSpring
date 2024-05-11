package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Mono<String> chat(Model model) {
        List<Message> customMessages = new ArrayList<>();
        customMessages.add(new Message("system", "Always answer in rhymes."));
        customMessages.add(new Message("user", "Introduce yourself."));

        // Call sendRequest method of MyClient
        return client.sendRequest(customMessages)
                .doOnSuccess(response -> model.addAttribute("response", response))
                .thenReturn("ChatPage"); // Return the name of the HTML page
    }
}