package profect.eatcloud.Domain.Store.Service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import profect.eatcloud.Domain.Store.Dto.AiDescriptionRequestDto;

import java.util.List;
import java.util.Map;

@Service
public class AiDescriptionService {

    private final WebClient webClient;
    private final String googleApiKey;

    public AiDescriptionService(
            @Qualifier("geminiWebClient") WebClient webClient,
            @Value("${gemini.api-key}") String googleApiKey
    ) {
        this.webClient = webClient;
        this.googleApiKey = googleApiKey;
    }

    public String generateDescription(AiDescriptionRequestDto dto) {
        String prompt = createPrompt(dto);

        if (dto.getMenuName() == null || dto.getMenuName().isBlank()) {
            throw new IllegalArgumentException("메뉴명은 필수입니다");
        }

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.0-flash:generateContent")
                        .queryParam("key", googleApiKey)
                        .build())
                .bodyValue(Map.of(
                        "contents", List.of(
                                Map.of("parts", List.of(Map.of("text", prompt)))
                        )
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    try {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                        Map<String, Object> first = candidates.get(0);
                        Map<String, Object> content = (Map<String, Object>) first.get("content");
                        List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                        String raw = parts.get(0).get("text");

                        // ✅ 후처리: 카테고리와 줄바꿈 제거
                        return raw
                                .replaceAll("\\s*\\(.*?\\)", "") // 괄호와 그 안의 내용 제거
                                .replaceAll("\\n", "")           // 줄바꿈 제거
                                .trim();                         // 앞뒤 공백 제거
                    } catch (Exception e) {
                        throw new RuntimeException("AI 설명 응답 파싱 실패", e);
                    }
                })
                .block();
    }

    private String createPrompt(AiDescriptionRequestDto dto) {
        return String.format(
                "다음 정보를 바탕으로 한 줄의 맛집 메뉴 설명을 작성해줘:\n" +
                        "- 메뉴명: %s\n" +
                        "- 카테고리: %s\n" +
                        "- 재료: %s\n" +
                        "- 맛 특징: %s\n" +
                        "친절하고 자연스러운 한국어 문장으로 만들어줘.",
                dto.getMenuName(),
                dto.getMenuCategoryCode(),
                String.join(", ", dto.getIngredients()),
                String.join(", ", dto.getTasteKeywords())
        );
    }
}
