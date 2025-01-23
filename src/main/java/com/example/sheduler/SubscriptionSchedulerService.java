package com.example.sheduler;

import com.example.entity.Book;
import com.example.repository.BookRepository;
import com.example.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@AllArgsConstructor
public class SubscriptionSchedulerService {
    private static final String DEFAULT_EMAIL_TO_SEND = "libraryWorkerEmail@gmail.com";
    private static final int OVERDUE_DAYS = 20;
    private static final int BATCH_SIZE = 100;

    private final BookRepository bookRepository;
    private final EmailService emailService;

    @Scheduled(cron = "${notification.schedule}")
    @SchedulerLock(name = "checkSubscriptionsAndNotifyIfOverdueLock", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")
    public void checkSubscriptionsAndNotifyIfOverdue() {

        var overdueDate = LocalDate.now().minusDays(OVERDUE_DAYS);
        int pacurrentPage = 0;
        boolean hasMoreRecords = true;

        while (hasMoreRecords) {
            Page<Book> bookPage = bookRepository.findBooksByPublicationDateBefore(overdueDate, PageRequest.of(pacurrentPage, BATCH_SIZE, Sort.by("id")));

            bookPage.getContent()
                    .forEach(book -> {
                        var emailToSend = book.getSubscription() != null ?
                                book.getSubscription().getUsername() : DEFAULT_EMAIL_TO_SEND;
                        emailService.sendEmail(emailToSend, String.format("Please, return book {%s}", book.getTitle() + book.getAuthor()));
                    });
            hasMoreRecords = bookPage.hasNext();
            pacurrentPage++;
        }
        ;
    }
}


