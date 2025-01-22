package com.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubscriptionResponseDto {
        String username;
        String userFullName;
        boolean active;
        List<SubscribedBookResponseDto> subscribedBooks;
}

