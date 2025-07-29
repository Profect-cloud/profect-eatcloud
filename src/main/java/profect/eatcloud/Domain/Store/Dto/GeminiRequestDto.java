package profect.eatcloud.Domain.Store.Dto;

public class GeminiRequestDto {
    private String prompt;

    public GeminiRequestDto(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}

