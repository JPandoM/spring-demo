package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class ApplicationShutdownService {

    public void shutdown() {
        // A new thread is used to allow the HTTP response to be sent before shutting down.
        new Thread(() -> {
            try {
                Thread.sleep(500); // Short delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.exit(1);
        }).start();
    }
}