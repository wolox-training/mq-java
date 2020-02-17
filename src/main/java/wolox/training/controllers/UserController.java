package wolox.training.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.*;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;


@RestController
@RequestMapping("/api/users")
@Api
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookController bookController;

    @GetMapping
    public Iterable findAll() {
        return userRepository.findAll();
    }

    /**
     * Find one user.
     *
     * @throws UserNotFoundException
     * @param id the id
     * @return the user
     */
    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) {
        return userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
    }

    /**
     * Find by user by username.
     *
     * @param username the user's username
     * @return the user
     */
    @GetMapping("/username/{username}")
    public Optional<User> findByTitle(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Create User.
     *
     * @param user the user to create
     * @return the user
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * Delete an existing user.
     *
     * @throws UserNotFoundException
     * @param id the path variable id of the user to find and delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
    }

    /**
     * Updates an existing user.
     *
     * @throws IdMismatchException if pathVariable id does not match body id.
     * @throws UserNotFoundException
     * @param user the request updated user to save
     * @param id   the path variable for the user id
     * @return the saved updated user
     */
    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable Long id) {
        if (user.getId() != id) {
            throw new IdMismatchException();
        }
        userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
        return userRepository.save(user);
    }


    /**
     * Assign Book to User.
     *
     * @param userId the user id
     * @param bookId the book id
     * @throws UserNotFoundException
     * @throws BookNotFoundException
     * @throws BookAlreadyOwnedException
     * @return the user
     */
    @ApiOperation(value = "Asigns a book to a user", response = User.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Book successfully assigned to user"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 404, message = "Book not found"),
        @ApiResponse(code = 400, message = "Book already owned exception")
    })
    @PostMapping("/{userId}/assignBook/{bookId}")
    public User assignBook(@PathVariable Long userId, @PathVariable Long bookId) {
        User user = findOne(userId);
        Book book = bookController.findOne(bookId);
        user.assignBook(book);
        return userRepository.save(user);
    }

    /**
     * Dessign a Book from a User.
     *
     * @param userId the user's id
     * @param bookId the user's id
     * @throws UserNotFoundException
     * @throws BookNotFoundException
     * @throws BookNotOwnedException
     * @return the user
     */
    @PostMapping("/{userId}/deassignBook/{bookId}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Book successfully assigned to user"),
        @ApiResponse(code = 404, message = "User not found"),
        @ApiResponse(code = 404, message = "Book not found"),
        @ApiResponse(code = 400, message = "Book not owned exception")
    })
    public User deassignBook(@PathVariable Long userId, @PathVariable Long bookId) {
        User user = findOne(userId);
        Book book = bookController.findOne(bookId);
        user.deassignBook(book);
        return userRepository.save(user);
    }

}