package com.example.service;

import com.example.entity.Book;
import com.example.entity.Subscription;
import com.example.mapper.SubscriptionMapper;
import com.example.repository.BookRepository;
import com.example.repository.SubscriptionRepository;
import com.example.dto.SubscriptionUploadDto;
import com.example.dto.SubscriptionResponseDto;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor

public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final BookRepository bookRepository;

    private static final int BATCH_SIZE = 500;


    public SubscriptionResponseDto getSubscription(String userFullName) {
        Subscription subscription = subscriptionRepository.findSubscriptionByUserFullName(userFullName);
        return subscriptionMapper.toDto(subscription);
    }

    public void parseFileWithSucription(MultipartFile file) throws IOException {

        List<Book> booksToSaveInBatch = new ArrayList<>(BATCH_SIZE);

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(file.getInputStream());
        ObjectMapper mapper = new ObjectMapper();

        if (parser.nextToken() != JsonToken.START_OBJECT) {
            logger.warn("Expected START_OBJECT token, got {}", parser.getCurrentToken());
            return;
        }

        if (!moveToDataArray(parser)) {
            return;
        }

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            SubscriptionUploadDto uploadDataDto = mapper.readValue(parser, SubscriptionUploadDto.class);
            processSubscriptionUploadDto(uploadDataDto, booksToSaveInBatch);
        }

        if (!booksToSaveInBatch.isEmpty()) {
            bookRepository.saveAll(booksToSaveInBatch);
        }
    }

    private boolean moveToDataArray(JsonParser parser) throws IOException {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            if ("data".equals(fieldName)) {
                parser.nextToken();
                return parser.currentToken() == JsonToken.START_ARRAY;
            }
        }
        logger.warn("Expected START_ARRAY token for 'data', got {}", parser.getCurrentToken());
        return false;
    }

    private void processSubscriptionUploadDto(SubscriptionUploadDto subscriptionUploadDto, List<Book> booksToSaveInBatch) {
        var subscription = subscriptionRepository.findSubscriptionByUsernameAndUserFullName(subscriptionUploadDto.getUsername(), subscriptionUploadDto.getUserFullName())
                .orElseGet(() -> {
                    var subscriptionToCreate = new Subscription(subscriptionUploadDto.getUsername(),
                            subscriptionUploadDto.getUserFullName(),
                            subscriptionUploadDto.isUserActive());
                    return subscriptionRepository.save(subscriptionToCreate);
                });

        var book = bookRepository.findBookByTitleAndAuthor(
                        subscriptionUploadDto.getBookName(),
                        subscriptionUploadDto.getBookAuthor())
                .orElse(new Book(subscriptionUploadDto.getBookName(), subscriptionUploadDto.getBookAuthor()));

        book.setSubscription(subscription);
        booksToSaveInBatch.add(book);

        if (booksToSaveInBatch.size() >= BATCH_SIZE) {
            bookRepository.saveAll(booksToSaveInBatch);
            booksToSaveInBatch.clear();
        }
    }
}
