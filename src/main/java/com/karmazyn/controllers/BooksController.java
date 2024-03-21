package com.karmazyn.controllers;

import com.karmazyn.dao.BookDao;
import com.karmazyn.dao.PersonDao;
import com.karmazyn.models.Book;
import com.karmazyn.models.Person;
import com.karmazyn.services.BookService;
import com.karmazyn.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;
    private final PersonService personService;
    private final BookDao bookDao;
    private final PersonDao personDao;

    @Autowired
    public BooksController(BookService bookService, PersonService personService, BookDao bookDao, PersonDao personDao) {
        this.bookService = bookService;
        this.personService = personService;
        this.bookDao = bookDao;
        this.personDao = personDao;
    }

    @GetMapping
    public String index(@RequestParam(name = "page") Optional<Integer>  page,
                        @RequestParam(name = "books_per_page") Optional<Integer> booksPerPage,
                        @RequestParam(name = "sort_by_year") Optional<Boolean> sortByYear, Model model) {
        if(booksPerPage.isPresent() && sortByYear.orElse(false)) {
            model.addAttribute("books",
                    bookService.findAll(PageRequest.of(page.orElse(0), booksPerPage.get(),
                            Sort.by("yearOfPublication"))));
        } else if (booksPerPage.isPresent()) {
            model.addAttribute("books",
                    bookService.findAll(PageRequest.of(page.orElse(0), booksPerPage.get())));
        } else if (sortByYear.orElse(false)) {
            model.addAttribute("books", bookService.findAll(Sort.by("yearOfPublication")));
        } else model.addAttribute("books", bookService.findAll());
        return "books/index";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, @ModelAttribute("person") Person person, Model model) {
        Book book = bookService.findOne(id);
        if (Objects.isNull(book)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book found");
        model.addAttribute("book", book);
        if (book.isFree()) model.addAttribute("people", personService.findAll());
        else model.addAttribute("owner", book.getOwner());
        return "books/show";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "books/new";
        bookService.save(book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        Book book = bookService.findOne(id);
        if (Objects.isNull(book)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book found");
        model.addAttribute("book", book);
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "books/edit";

        bookService.update(id, book);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person person) {
        bookService.assign(bookService.findOne(id), person);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/free")
    public String free(@PathVariable("id") int id) {
        bookService.free(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "search_query") Optional<String> searchQuery, Model model) {
        if (searchQuery.isPresent()) {
            List<Book> books = bookService.findAllByTitleStartingWith(searchQuery.get());
            if (books.isEmpty()) model.addAttribute("is_found", false);
            else {
                model.addAttribute("books", books);
                model.addAttribute("is_found", true);
            }
        }
        return "books/search";
    }

}
