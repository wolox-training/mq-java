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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotOwnedException;
import wolox.training.exceptions.IdMismatchException;
import wolox.training.exceptions.EntityNotFoundException;
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

    /**
     * Find all users.
     *
     * @param username optional query param to find by the user's username
     * @return the found users
     */
    @GetMapping
    public Iterable findAll(@RequestParam String username) {
        if (username != null && !username.isEmpty())
            return userRepository.findByUsername(username);
        return userRepository.findAll();
    };

    /**
     * Find one user.
     *
     * @throws EntityNotFoundException
     * @param id the id
     * @return the user
     */
    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new EntityNotFoundException(User.class);
        return user.get();
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
     * @throws EntityNotFoundException
     * @param id the path variable id of the user to find and delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new EntityNotFoundException(User.class);
        userRepository.deleteById(id);
    }

    /**
     * Updates an existing user.
     *
     * @throws IdMismatchException if pathVariable id does not match body id.
     * @throws EntityNotFoundException
     * @param user the request updated user to save
     * @param id   the path variable for the user id
     * @return the saved updated user
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@RequestBody User user, @PathVariable Long id) {
        if (user.getId() != id) {
            throw new IdMismatchException("user");
        }
        Optional<User> dbUser = userRepository.findById(id);
        if (!dbUser.isPresent())
            throw new EntityNotFoundException(User.class);
        userRepository.save(user);
    }


    /**
     * Assign Book to User.
     *
     * @param userId the user id
     * @param bookId the book id
     * @throws EntityNotFoundException
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignBook(@PathVariable Long userId, @PathVariable Long bookId) {
        User user = findOne(userId);
        Book book = bookController.findOne(bookId);
        user.assignBook(book);
        userRepository.save(user);
    }

    /**
     * Dessign a Book from a User.
     *
     * @param userId the user's id
     * @param bookId the user's id
     * @throws EntityNotFoundException
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deassignBook(@PathVariable Long userId, @PathVariable Long bookId) {
        User user = findOne(userId);
        Book book = bookController.findOne(bookId);
        user.deassignBook(book);
        userRepository.save(user);
    }

}