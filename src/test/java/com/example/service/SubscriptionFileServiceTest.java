package com.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionFileServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonSchema validationSchema;

    @Mock
    MultipartFile multipartFile;

    @InjectMocks
    private SubscriptionFileService subscriptionFileService;


    @Test
    void validateFile_ShouldThrowException_WhenFileIsEmpty() {
        when(multipartFile.isEmpty()).thenReturn(true);
        when(multipartFile.getName()).thenReturn("fileName");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionFileService.validateFile(multipartFile, validationSchema);
        });

        assertEquals("File {fileName} is empty", exception.getMessage());
    }

    @Test
    void validateFile_ShouldThrowException_WhenJsonValidationFails() throws Exception {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("{\"key\":\"value\"}".getBytes());
        ValidationMessage validationMessage = mock(ValidationMessage.class);
        when(validationMessage.getMessage()).thenReturn("Sample validation error");

        when(validationSchema.validate(any())).thenReturn(Set.of(validationMessage));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subscriptionFileService.validateFile(multipartFile, validationSchema);
        });

        assertTrue(exception.getMessage().contains("Json file validation errors: Sample validation error"));
    }

    @Test
    void validateFile_ShouldNotThrowException_WhenValidationPasses() throws Exception {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("{\"key\":\"value\"}".getBytes());
        when(validationSchema.validate(any())).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> {
            subscriptionFileService.validateFile(multipartFile, validationSchema);
        });
    }

    @Test
    void safeFile_ShouldSaveFile_WhenFileIsValid() throws Exception {
        String directory = "test-directory/";
        Files.createDirectories(Paths.get(directory));

        when(multipartFile.getName()).thenReturn("testfile.txt");
        doNothing().when(multipartFile).transferTo(any(File.class));

        File savedFile = subscriptionFileService.safeFile(multipartFile, directory);

        assertNotNull(savedFile);
        assertEquals("testfile.txt", savedFile.getName());

        // Clean up
        Files.deleteIfExists(Paths.get(directory));
    }

    @Test
    void safeFile_ShouldThrowException_WhenDirectoryNotExists() throws Exception {
        String notExistingDirectory = "not-existing-directory/";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subscriptionFileService.safeFile(multipartFile, notExistingDirectory);
        });

        assertEquals("Directory does not exist: not-existing-directory/", exception.getMessage());
    }

    @Test
    void safeFile_ShouldThrowException_WhenIOExceptionOccurs() throws Exception {
        String directory = "test-directory/";
        Files.createDirectories(Paths.get(directory));
        doThrow(new IOException("Mocked IO exception"))
                .when(multipartFile).transferTo(any(File.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subscriptionFileService.safeFile(multipartFile, directory);
        });

        assertEquals("Failed to save file", exception.getMessage());

        // Clean up
        Files.deleteIfExists(Paths.get(directory));
    }
}