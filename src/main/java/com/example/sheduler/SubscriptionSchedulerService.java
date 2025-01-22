package com.example.sheduler;

import com.example.entity.Subscription;
import com.example.repository.SubscriptionRepository;
import com.example.service.EmailService;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Component
@AllArgsConstructor
public class SubscriptionSchedulerService {
    private static final String DEFAULT_EMAIL_TO_SEND = "libraryWorkerEmail@gmail.com";
    private static final int OVERDUE_DAYS = 20;
    private static final int BATCH_SIZE = 100;

    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    @Scheduled(cron = "${notification.schedule}")
    @SchedulerLock(name = "checkSubscriptionsAndNotifyIfOverdueLock", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")
    public void checkSubscriptionsAndNotifyIfOverdue() {
        var today = LocalDate.now();
        int pageNumber = 0;
        long procededSubscription = 0;
        long totalSubscriptions = subscriptionRepository.count();

        do {
            subscriptionRepository.findAll(PageRequest.of(pageNumber, BATCH_SIZE)).getContent()
                    .stream()
                    .map(Subscription::getSubscribedBooks)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(book -> book.getPublicationDate() != null)
                    .filter(book -> book.getPublicationDate().plusDays(OVERDUE_DAYS).isBefore(today))
                    .forEach(book -> {
                        var emailToSend = book.getSubscription() != null ?
                                book.getSubscription().getUsername() : DEFAULT_EMAIL_TO_SEND;
                        emailService.sendEmail(emailToSend, String.format("Bерните книгу {%s}", book.getTitle() + book.getAuthor()));
                    });

            procededSubscription += BATCH_SIZE;
            pageNumber++;
        } while (procededSubscription < totalSubscriptions);
    }
}


