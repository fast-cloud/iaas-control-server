package iaas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // Spring Boot 3 / Spring MVC 6에서는 credentials=true 와 함께 와일드카드를 쓰려면 patterns 사용
                        .allowedOriginPatterns("*")         // 모든 Origin 허용
                        .allowedMethods("*")                // 모든 HTTP Method 허용
                        .allowedHeaders("*")                // 모든 요청 헤더 허용
                        .exposedHeaders("*")                // 모든 응답 헤더 노출
                        .allowCredentials(true)             // 쿠키/Authorization 등 허용 (완전 완화)
                        .maxAge(3600);                      // Preflight 캐시 시간 (초)
            }
        };
    }
}