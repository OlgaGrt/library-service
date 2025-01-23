package com.example.service;

import com.example.exception.NotFoundException;
import com.example.mapper.SubscriptionMapper;
import com.example.repository.SubscriptionRepository;
import com.example.dto.SubscriptionResponseDto;
import com.networknt.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionService {
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    private final JsonSchema subscriptionJsonValidationSchema;

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionFileService subscriptionFileService;
    private final SubscriptionFileBatchProcessingService subscriptionFileBatchProcessingService;

    public SubscriptionResponseDto getSubscription(String userFullName) {
        var subscription = subscriptionRepository.findSubscriptionByUserFullName(userFullName)
                .orElseThrow(NotFoundException::new);

        return subscriptionMapper.toDto(subscription);
    }

    public String processFile(MultipartFile file) {
        subscriptionFileService.validateFile(file, subscriptionJsonValidationSchema);
        var savedFile = subscriptionFileService.safeFile(file, TMP_DIR);
        subscriptionFileBatchProcessingService.launchSubscriptionsFileProcessJob(savedFile.getAbsolutePath());
        return "file validated and accepted";
    }
}
