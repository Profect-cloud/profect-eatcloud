package profect.eatcloud.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${toss.api.base-url:https://api.tosspayments.com}")
    private String tossApiBaseUrl;

    @Bean(name = "tossWebClient")
    public WebClient tossWebClient() {
        return WebClient.builder()
                .baseUrl(tossApiBaseUrl)
                .build();
    }
} 