package com.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionBatchService {

    private JobLauncher jobLauncher;
    private Job job;

    @Async
    public void launchSubscriptionsFileProcessJob(String filePath) {
        try {
            var jobParameters = new JobParametersBuilder()
                    .addString("input.file.path", filePath)
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
