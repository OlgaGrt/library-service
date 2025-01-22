package com.example.repository;

import com.example.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Subscription findSubscriptionByUserFullName(String userFullName);
    Optional<Subscription> findSubscriptionByUsernameAndUserFullName(String username, String userFullName);
}
