package com.example.dto;

import lombok.Data;

@Data
public class SubscriptionUploadDto {
    String username;
    String userFullName;
    boolean userActive;
    String bookName;
    String bookAuthor;
}
