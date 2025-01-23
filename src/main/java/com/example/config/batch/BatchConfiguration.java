package com.example.config.batch;

import com.example.dto.SubscriptionUploadDto;
import com.example.entity.Subscription;
import com.example.repository.BookRepository;
import com.example.repository.SubscriptionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.Executor;

@Configuration
public class BatchConfiguration {

    @Bean
    public Job job(final JobRepository jobRepository, final PlatformTransactionManager transactionManager,
                   final SubscriptionRepository subscriptionRepository, final BookRepository bookRepository) {
        return new JobBuilder("importSubscriptionsStep", jobRepository)
                .start(importVisitorsStep(jobRepository, transactionManager, subscriptionRepository, bookRepository))
                .build();
    }

    @Bean
    public Step importVisitorsStep(final JobRepository jobRepository, final PlatformTransactionManager transactionManager,
                                   final SubscriptionRepository subscriptionRepository, BookRepository bookRepository) {
        return new StepBuilder("importSubscriptionsStep", jobRepository)
                .<SubscriptionUploadDto, Subscription>chunk(100, transactionManager)
                .reader(jsonFileReader(null))
                .processor(itemProcessor(subscriptionRepository, bookRepository))
                .writer(visitorsItemWriter(subscriptionRepository))
                .build();
    }

    @Bean
    public SubscriptionItemProcessor itemProcessor(SubscriptionRepository subscriptionRepository, BookRepository bookRepository) {
        return new SubscriptionItemProcessor(subscriptionRepository, bookRepository);
    }

    @Bean
    public SubscriptionItemWriter visitorsItemWriter(final SubscriptionRepository subscriptionRepository) {
        return new SubscriptionItemWriter(subscriptionRepository);
    }

    @Bean
    @StepScope
    public JsonItemReader<SubscriptionUploadDto> jsonFileReader(@Value("#{jobParameters['input.file.path']}") final String fileToRead) {
        return new JsonItemReader<>(new FileSystemResource(fileToRead),
                new JacksonJsonObjectReader<>(SubscriptionUploadDto.class));
    }

    @Bean("batchTaskExecutor")
    public Executor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("batch-");
        executor.initialize();
        return executor;
    }

}