package wolox.training.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import wolox.training.exceptions.EntityNotFoundException;
import wolox.training.exceptions.ExternalServiceException;
import wolox.training.exceptions.IdMismatchException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibrary;

@RestController
@RequestMapping("/api/books")
@Api
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OpenLibrary openLibrary;

    /**
     * Find all books.
     *
     * @param title optional query param to find by the ${@link Book}'s title
     * @param author optional query param to find by the ${@link Book}'s author
     * @param image optional query param to find by the ${@link Book}'s image
     * @param subtitle optional query param to find by the ${@link Book}'s subtitle
     * @param publisher optional query param to find by the ${@link Book}'s publisher
     * @param year optional query param to find by the ${@link Book}'s year
     * @param pages optional query param to find by the ${@link Book}'s pages
     * @param isbn optional query param to find by the ${@link Book}'s isbn
     * @param genre optional query param to find by the ${@link Book}'s genre
     * @return the found books
     */
    @GetMapping
    public Page<Book> findAll(
        Pageable pageable,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String image,
        @RequestParam(required = false) String subtitle,
        @RequestParam(required = false) String publisher,
        @RequestParam(required = false) String year,
        @RequestParam(required = false) Integer pages,
        @RequestParam(required = false) String isbn,
        @RequestParam(required = false) String genre
        ) {
        return bookRepository.findAllCustom(
            title,
            author,
            image,
            subtitle,
            publisher,
            year,
            pages,
            isbn,
            genre,
            pageable
        );
    };

    /**
     * Find one book by id.
     *
     * @throws EntityNotFoundException
     * @param id the id
     * @return the {@link Book}
     */
    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Book.class));
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
     * @throws EntityNotFoundException
     * @param id the path variable id of the book to find and delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Book.class));
        bookRepository.deleteById(id);
    }

    /**
     * Updates an existing book.
     *
     * @throws IdMismatchException if pathVariable id does not match body id.
     * @throws EntityNotFoundException
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
            throw new IdMismatchException(Book.class);
        }
        bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(Book.class));
        bookRepository.save(book);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity findByIsbn(@PathVariable String isbn) throws ExternalServiceException {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            return new ResponseEntity(book.get(), HttpStatus.OK);
        }

        return new ResponseEntity(
            openLibrary.tryGetBookByIsbn(isbn)
                .orElseThrow(() -> new EntityNotFoundException(Book.class)),
            HttpStatus.CREATED
        );
    }
}