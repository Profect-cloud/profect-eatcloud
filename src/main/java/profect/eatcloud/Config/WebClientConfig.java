package profect.eatcloud.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Bean
    @Qualifier("tossWebClient")
    public WebClient tossWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}