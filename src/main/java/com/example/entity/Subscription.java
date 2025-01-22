package com.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@NoArgsConstructor
@Getter
@Setter
public class Subscription {
    @Id
    private UUID id = UUID.randomUUID();

    private String username;
    private String userFullName;
    private boolean isActive;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Book> subscribedBooks;

    public Subscription(String username, String userFullName, boolean isActive) {
        this.username = username;
        this.userFullName = userFullName;
        this.isActive = isActive;
        this.subscribedBooks = new ArrayList<>();
    }
}
