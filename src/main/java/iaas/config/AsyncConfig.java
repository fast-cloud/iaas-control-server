package iaas.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * - 시퀀스 다이어그램 3의 "par (병렬)" 구현
 * - OpenStack VM 생성을 별도 스레드에서 실행
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 기본 스레드 풀 크기
        executor.setCorePoolSize(5);
        
        // 최대 스레드 풀 크기
        executor.setMaxPoolSize(10);
        
        // 큐 용량 (대기 가능한 작업 수)
        executor.setQueueCapacity(25);
        
        // 스레드 이름 접두사
        executor.setThreadNamePrefix("OpenStack-Async-");
        
        // 스레드 풀 초기화
        executor.initialize();
        
        log.info("Async Executor initialized with core pool size: {}, max pool size: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize());
        
        return executor;
    }
}

