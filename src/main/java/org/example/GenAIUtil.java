package org.example;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class GenAIUtil {
    private static final String API_KEY = "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";

    public static String generateTestData(String prompt) {
        OpenAiService service = new OpenAiService(API_KEY);
        CompletionRequest request = CompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .prompt(prompt)
                .maxTokens(100)
                .build();
        return service.createCompletion(request).getChoices().get(0).getText();
    }
}
