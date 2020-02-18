package wolox.training.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.IdMismatchException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

@RestController
@RequestMapping("/api/books")
@Api
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    /**
     * Find all books.
     *
     * @param title optional query param to find by the book's title
     * @return the found books
     */
    @GetMapping
    public Iterable findAll(@RequestParam String title) {
        if (title != null && !title.isEmpty())
            return bookRepository.findByTitle(title);
        return bookRepository.findAll();
    };

    /**
     * Find one book by id.
     *
     * @throws BookNotFoundException
     * @param id the id
     * @return the book
     */
    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) {
        return bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
    }

    /**
     * Create book.
     *
     * @param book the book
     * @return the book
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    /**
     * Delete an existing book.
     *
     * @throws BookNotFoundException
     * @param id the path variable id of the book to find and delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    /**
     * Updates an existing book.
     *
     * @throws IdMismatchException if pathVariable id does not match body id.
     * @throws BookNotFoundException
     * @param book the request updated book to save
     * @param id   the path variable for the book id
     * @return the saved updated book
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "Updates all the fields of a given book", response = Book.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Book successfully updated"),
        @ApiResponse(code = 404, message = "Book not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new IdMismatchException("book");
        }
        bookRepository.findById(id)
            .orElseThrow(BookNotFoundException::new);
        bookRepository.save(book);
    }
}