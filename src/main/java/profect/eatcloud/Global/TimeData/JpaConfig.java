package profect.eatcloud.global.timedata;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
	basePackages = "profect.eatcloud",
	repositoryBaseClass = BaseTimeRepositoryImpl.class
)
public class JpaConfig {
}
