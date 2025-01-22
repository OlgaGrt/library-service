package com.example.config.batch;

import com.example.entity.Subscription;
import com.example.repository.SubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SubscriptionItemWriter implements ItemWriter<Subscription> {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void write(Chunk<? extends Subscription> chunk) throws Exception {
        subscriptionRepository.saveAll(chunk.getItems());
    }
}
