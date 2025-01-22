package com.example.sheduler;

import com.example.entity.Subscription;
import com.example.repository.SubscriptionRepository;
import com.example.service.EmailService;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Component
@AllArgsConstructor
public class SubscriptionShedulerService {
    private static final String DEFAULT_EMAIL_TO_SEND = "libraryWorkerEmail@gmail.com";
    private static final int OVERDUE_DAYS = 20;

    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    @Scheduled(cron = "${notification.schedule}")
    @SchedulerLock(name = "x", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")
    public void checkSubscriptionsAndNotifyIfOverdue() {
        LocalDate today = LocalDate.now();
        subscriptionRepository.findAll()
                .stream()
                .map(Subscription::getSubscribedBooks)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(book -> book.getPublicationDate() != null)
                .filter(book -> book.getPublicationDate().plusDays(OVERDUE_DAYS).isBefore(today))
                .forEach(book ->
                {
                    var emailToSend = book.getSubscription() != null ? book.getSubscription().getUsername() : DEFAULT_EMAIL_TO_SEND;
                    emailService.sendEmail(emailToSend, String.format("верните книгу {%s}", book.getTitle() + book.getAuthor()));
                });
    }
}
