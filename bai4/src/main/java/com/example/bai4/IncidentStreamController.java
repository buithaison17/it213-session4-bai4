package com.example.bai4;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/incident")
@RequiredArgsConstructor
public class IncidentStreamController {
    private final ChatModel chatModel;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String rawText,
                               @RequestParam(defaultValue = "0.5") Double temp,
                               @RequestParam(defaultValue = "1000") Integer maxTokens,
                               HttpServletResponse response
    ) {
        response.addHeader("X-Accel-Buffering", "no");
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .temperature(temp)
                .maxTokens(maxTokens)
                .build();
        Prompt prompt = new Prompt(rawText, chatOptions);
        return chatModel.stream(prompt).map(result -> Optional.ofNullable(result.getResult())
                .map(res -> res.getOutput().getText())
                .orElse("")
        );
    }
}
