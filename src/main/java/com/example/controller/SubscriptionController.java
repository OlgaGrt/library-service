package com.example.controller;

import com.example.dto.SubscriptionResponseDto;
import com.example.service.SubscriptionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/v1/subscriptions")
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/{userFullName}")
    public SubscriptionResponseDto getText(@PathVariable(name = "userFullName") String userFullName) {
        return subscriptionService.getSubscription(userFullName);
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadSubscriptions(@RequestParam("file") MultipartFile file) throws IOException {
        subscriptionService.parseFileWithSucription(file);
        return ResponseEntity.ok("file parsed");
    }
}
