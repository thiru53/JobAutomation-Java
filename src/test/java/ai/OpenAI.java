package ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.json.JSONObject;

public class OpenAI {

    private final String apiKey = System.getenv("OPENAI_API_KEY");
    private final HttpClient client;

    public OpenAI() {
        this.client = HttpClient.newHttpClient();
    }

    public String generateAnswer(String question, String resumeData, String profileSummary) {
        String prompt = buildPrompt(question, resumeData, profileSummary);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
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
            System.err.println("AI generation failed: " + e.getMessage());
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
                "model", "gpt-3.5-turbo",
                "messages", new org.json.JSONArray()
                        .put(new JSONObject(Map.of("role", "system", "content", "You are a helpful assistant.")))
                        .put(new JSONObject(Map.of("role", "user", "content", prompt))),
                "temperature", 0.7
        ));
        return body.toString();
    }
}
