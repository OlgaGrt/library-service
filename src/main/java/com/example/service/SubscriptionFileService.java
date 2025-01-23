package com.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionFileService {
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    private JsonSchema subscriptionUploadDtoJsonSchema;
    private ObjectMapper objectMapper;

    public void validateFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            var jsonNodeToValidate = objectMapper.readTree(multipartFile.getBytes());
            var errors = subscriptionUploadDtoJsonSchema.validate(jsonNodeToValidate);
            if (!errors.isEmpty()) {
                var errorMessages = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining(", "));

                log.info("file: {} contains validations errors: {}", multipartFile.getOriginalFilename(), errorMessages);

                throw new RuntimeException("Json file validation errors: " + errorMessages);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File safeFile(MultipartFile multipartFile) {
        try {
            var savedFile = new File(TMP_DIR + multipartFile.getOriginalFilename());
            multipartFile.transferTo(savedFile);

            log.info("File saved to: {}", savedFile.getAbsolutePath());

            return savedFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
    }

}


