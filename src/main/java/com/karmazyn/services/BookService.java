package com.karmazyn.services;

import com.karmazyn.models.Book;
import com.karmazyn.models.Person;
import com.karmazyn.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BooksRepository booksRepository;

    @Autowired
    public BookService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public Page<Book> findAll(Pageable pageable) {
        return booksRepository.findAll(pageable);
    }

    public List<Book> findAll(Sort sort) {
        return booksRepository.findAll(sort);
    }

    public List<Book> findAllByTitleStartingWith(String startingWith) {
        if (startingWith.isEmpty()) return Collections.emptyList();
        return booksRepository.findByTitleStartingWith(startingWith);
    }

    public Book findOne(int id) {
        return booksRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void assign(Book book, Person person) {
        book.setOwner(person);
        book.setTakenAt(LocalDateTime.now());
        booksRepository.save(book);
    }

    @Transactional
    public void free(int id) {
        Book book = booksRepository.findById(id).orElse(null);
        if (Objects.isNull(book)) throw new IllegalArgumentException("Book not found");
        book.setOwner(null);
        book.setTakenAt(null);
    }
}
