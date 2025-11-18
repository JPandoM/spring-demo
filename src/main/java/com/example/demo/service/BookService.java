package com.example.demo.service;

import com.example.demo.mapper.BookMapper;
import com.example.demo.model.dtos.BookDTO;
import com.example.demo.model.entities.Book;
import com.example.demo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id).map(bookMapper::toDto);
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        book.setId(null); // Ensure ID is null for creation to let JPA generate it
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    public Optional<BookDTO> updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(bookDTO.title());
                    existingBook.setAuthor(bookDTO.author());
                    existingBook.setNPages(bookDTO.nPages());
                    existingBook.setNChapters(bookDTO.nChapters());
                    Book updatedBook = bookRepository.save(existingBook);
                    return bookMapper.toDto(updatedBook);
                });
    }

    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
