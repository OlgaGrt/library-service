package com.example.service;

import com.example.exception.JsonValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionFileService {

    private ObjectMapper objectMapper;

    public void validateFile(MultipartFile multipartFile, JsonSchema validationSchema) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException(String.format("File {%s} is empty", multipartFile.getName()));
        }

        try {
            var jsonNodeToValidate = objectMapper.readTree(multipartFile.getBytes());
            var errors = validationSchema.validate(jsonNodeToValidate);

            if (!errors.isEmpty()) {
                var errorMessages = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining(", "));

                log.info("File: {} contains validation errors: {}", multipartFile.getOriginalFilename(), errorMessages);

                throw new JsonValidationException("Json file validation errors: " + errorMessages);
            }
        } catch (IOException e) {
            log.error("Error while validating file {}: {}", multipartFile.getName(), e.getMessage(), e);
            throw new RuntimeException("Error while validating file", e);
        }
    }

    public File safeFile(MultipartFile multipartFile, String directory) {
        if (!Files.exists(Path.of(directory))) {
            var errorMessage = "Directory does not exist: " + directory;
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            var savedFile = new File(directory + multipartFile.getName());
            multipartFile.transferTo(savedFile);

            log.info("File saved to: {}", savedFile.getAbsolutePath());

            return savedFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
    }

}


