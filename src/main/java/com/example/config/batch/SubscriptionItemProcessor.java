package com.example.config.batch;

import com.example.dto.SubscriptionUploadDto;
import com.example.entity.Book;
import com.example.entity.Subscription;
import com.example.repository.BookRepository;
import com.example.repository.SubscriptionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SubscriptionItemProcessor implements ItemProcessor<SubscriptionUploadDto, Subscription> {

    SubscriptionRepository subscriptionRepository;
    BookRepository bookRepository;

    @Override
    public Subscription process(final SubscriptionUploadDto subscriptionUploadDto) {
        log.info("subscriptionUploadDto: {}", subscriptionUploadDto.getBookAuthor());

        var subscription = getOrCreateSubscription(subscriptionUploadDto);
        var book = getOrCreateBook(subscriptionUploadDto);

        subscription.getSubscribedBooks().add(book);
        book.setSubscription(subscription);

        return subscription;
    }

    private Subscription getOrCreateSubscription(SubscriptionUploadDto subscriptionUploadDto) {
        return subscriptionRepository.findSubscriptionByUsernameAndUserFullName(subscriptionUploadDto.getUsername(), subscriptionUploadDto.getUserFullName())
                .orElseGet(() -> {
                    var newSubscription = new Subscription(subscriptionUploadDto.getUsername(),
                            subscriptionUploadDto.getUserFullName(),
                            subscriptionUploadDto.isUserActive());
                    return subscriptionRepository.save(newSubscription);
                });
    }

    private Book getOrCreateBook(SubscriptionUploadDto subscriptionUploadDto) {
        return bookRepository.findBookByTitleAndAuthor(subscriptionUploadDto.getBookName(), subscriptionUploadDto.getBookAuthor())
                .orElseGet(() -> new Book(subscriptionUploadDto.getBookName(), subscriptionUploadDto.getBookAuthor()));
    }
}