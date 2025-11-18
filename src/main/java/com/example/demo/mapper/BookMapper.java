package com.example.demo.mapper;

import com.example.demo.model.dtos.BookDTO;
import com.example.demo.model.entities.Book;

import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDTO toDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getNPages(), book.getNChapters());
    }

    public Book toEntity(BookDTO bookDTO) {
        if (bookDTO == null) {
            return null;
        }
        Book book = new Book();
        book.setId(bookDTO.id());
        book.setTitle(bookDTO.title());
        book.setAuthor(bookDTO.author());
        book.setNPages(bookDTO.nPages());
        book.setNChapters(bookDTO.nChapters());
        return book;
    }
}
