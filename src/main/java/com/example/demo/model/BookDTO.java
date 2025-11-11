package com.example.demo.model;

import java.util.UUID;

public record BookDTO(UUID id, String title, String author, int nPages, int nChapters) {

}