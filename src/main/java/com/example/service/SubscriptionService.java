package com.example.service;

import com.example.entity.Subscription;
import com.example.exception.NotFoundException;
import com.example.mapper.SubscriptionMapper;
import com.example.repository.SubscriptionRepository;
import com.example.dto.SubscriptionResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionFileService subscriptionFileService;
    private final SubscriptionBatchService subscriptionBatchService;

    public SubscriptionResponseDto getSubscription(String userFullName) {
        var subscription = subscriptionRepository.findSubscriptionByUserFullName(userFullName)
                .orElseThrow(NotFoundException::new);

        return subscriptionMapper.toDto(subscription);
    }

    public String processFile(MultipartFile file) throws IOException {
        var savedFile = subscriptionFileService.validateAndSafeFile(file);
        subscriptionBatchService.launchSubscriptionsFileProcessJob(savedFile.getAbsolutePath());
        return "file accepted";
    }
}
