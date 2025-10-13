package ai;

public class AiUtility {

    public static AzureOpenAI getAzureOpenAi() {
        return new AzureOpenAI(
                System.getenv("AZURE_OPENAI_ENDPOINT"),
                System.getenv("AZURE_OPENAI_API_KEY"),
                System.getenv("AZURE_OPENAI_DEPLOYMENT")
        );
    }

    public static OpenAI getOpenAi() {
        return new OpenAI();
    }
}
