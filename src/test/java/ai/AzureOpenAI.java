package ai;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import java.util.Map;

public class AzureOpenAI {

    private final String endpoint;
    private final String apiKey;
    private final String deploymentName;
    private final HttpClient client;

    public AzureOpenAI(String endpoint, String apiKey, String deploymentName) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.deploymentName = deploymentName;
        this.client = HttpClient.newHttpClient();
    }

    public String generateAnswer(String question, String resumeData, String profileSummary) {
        String prompt = buildPrompt(question, resumeData, profileSummary);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint + "/openai/deployments/" + deploymentName + "/chat/completions?api-version=2023-07-01-preview"))
                    .header("Content-Type", "application/json")
                    .header("api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(prompt)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

        } catch (Exception e) {
            System.err.println("Azure AI generation failed: " + e.getMessage());
            return "Unable to generate answer at this time.";
        }
    }

    private String buildPrompt(String question, String resumeData, String profileSummary) {
        return "You are a job applicant. Based on the following resume and LinkedIn profile, answer the question professionally.\n\n"
                + "Resume:\n" + resumeData + "\n\n"
                + "LinkedIn Profile:\n" + profileSummary + "\n\n"
                + "Question:\n" + question;
    }

    private String buildRequestBody(String prompt) {
        JSONObject body = new JSONObject(Map.of(
                "messages", new org.json.JSONArray()
                        .put(new JSONObject(Map.of("role", "system", "content", "You are a helpful assistant.")))
                        .put(new JSONObject(Map.of("role", "user", "content", prompt))),
                "temperature", 0.7
        ));
        return body.toString();
    }
}
