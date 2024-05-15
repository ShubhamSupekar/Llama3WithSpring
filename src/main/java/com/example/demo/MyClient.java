package com.example.demo;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MyClient {

    private final WebClient webClient;

    public MyClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:1234").build();
    }

    public Flux<String> sendRequest(List<Message> customMessages) {
        String url = "/v1/chat/completions";

        // Create request body object
        ChatRequest request = new ChatRequest();
        request.setModel("lmstudio-community/Meta-Llama-3-8B-Instruct-GGUF");
        request.setTemperature(0.7);
        request.setMaxTokens(-1);
        request.setStream(true);
        request.setMessages(customMessages);

        // Send the request and handle the streaming response
        Flux<String> responseFlux = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::extractContent)
                .filter(content -> content != null && !content.isEmpty())
                .map(content -> content.replaceAll("\\\\n", "\n"));

        return responseFlux;
    }

    private String extractContent(String response) {
        int startIndex = response.indexOf("\"content\":");
        if (startIndex != -1) { // Check if "content" is found
            startIndex = response.indexOf("\"", startIndex + "\"content\":".length()); // Find the opening quote after "content"
            if (startIndex != -1) {
                int endIndex = response.indexOf("\"", startIndex + 1); // Find the closing quote after content value
                if (endIndex != -1) {
                    return response.substring(startIndex + 1, endIndex); // Add 1 to startIndex to remove the opening quote
                }
            }
        }
        return null; // Return null if content is not found or if the indexes are invalid
    }
}
