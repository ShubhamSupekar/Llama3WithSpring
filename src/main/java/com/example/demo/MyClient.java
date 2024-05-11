package com.example.demo;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyClient {

    public Mono<String> sendRequest(List<Message> customMessages) {
        String url = "http://localhost:1234/v1/chat/completions";

        // Create request body object
        ChatRequest request = new ChatRequest();
        request.setModel("lmstudio-community/Meta-Llama-3-8B-Instruct-GGUF");
        request.setTemperature(0.7);
        request.setMaxTokens(-1);
        request.setStream(true);
        request.setMessages(customMessages);

        WebClient webClient = WebClient.create();

        // Send the request
        Flux<String> responseFlux = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(String.class);

        // Collect all responses and concatenate them into a single string
        Mono<String> responseMono = responseFlux.collectList()
                .map(responses -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String response : responses) {
                        String content = extractContent(response);
                        if (content != null) {
                            content = content.replaceAll("\\\\n", "\n"); // Replace "\n" with actual line breaks
                            stringBuilder.append(content).append(" "); // Append each response
                        }
                    }
                    return stringBuilder.toString().trim(); // Remove trailing whitespace
                });

        return responseMono;
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
