package org.wyona.webapp.config;

import java.util.concurrent.Executor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "email.executor")
public class AsyncExecutorConfig
{

  private int maxThreads;
  private int maxPoolSize;
  private int corePoolSize;

  @Bean(name = "emailAsyncExecutor")
  public Executor asyncExecutor()
  {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(maxThreads);
    executor.setThreadNamePrefix("EmailAsyncExecutorThread-");
    executor.initialize();
    return executor;
  }

  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
  }

  public void setMaxPoolSize(int maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

  public void setCorePoolSize(int corePoolSize) {
    this.corePoolSize = corePoolSize;
  }
}
