package wolox.training.controllers;

import static wolox.training.configuration.ServerSecurityConfig.encodePassword;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
     * Find all {@link User}s.
     *
     * @param name optional query param to find by the {@link User}'s name
     * @param username optional query param to find by the {@link User}'s username
     * @param role optional query param to find by the {@link User}'s role
     * @param birthDate optional query param to find by the {@link User}'s birthDate
     * @return the found {@link User}s
     */
    @GetMapping
    public Page<User> findAll(
        Pageable pageable,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) LocalDate birthDate
    ) {
        return userRepository.findAllCustom(name, username, role, birthDate, pageable);
    };

    /**
     * Find one {@link User}.
     *
     * @throws EntityNotFoundException
     * @param id the id
     * @return the {@link User}
     */
    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(User.class));
    }

    /**
     * Create User.
     *
     * @param user the {@link User} to create
     * @return the {@link User}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        user.setPassword(encodePassword(user.getPassword()));
        User dbUser = userRepository.save(user);
        return dbUser;
    }

    /**
     * Delete an existing {@link User}.
     *
     * @throws EntityNotFoundException
     * @param id the path variable id of the {@link User} to find and delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(User.class));
        userRepository.deleteById(id);
    }

    /**
     * Updates an existing user.
     *
     * @throws IdMismatchException if pathVariable id does not match body id.
     * @throws EntityNotFoundException
     * @param user the request updated {@link User} to save
     * @param id   the path variable for the {@link User} id
     * @return the saved updated {@link User}
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@RequestBody User user, @PathVariable Long id) {
        if (user.getId() != id)
            throw new IdMismatchException(User.class);
        userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(User.class));
        userRepository.save(user);
    }


    /**
     * Assign Book to User.
     *
     * @param userId the {@link User} id
     * @param bookId the {@link Book} id
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

    /**
     * Update password of a {@link User}.
     *
     * @param id the user's id
     * @param user the user
     * @throws EntityNotFoundException
     * @throws BookNotOwnedException
     * @return the user
     */
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable Long id, @RequestBody User user) {
        if (user.getId() != id)
            throw new IdMismatchException(User.class);
        User dbUser = userRepository.findById(id).orElse(null);
        dbUser.setPassword(encodePassword(user.getPassword()));
        userRepository.save(dbUser);
    }


    /**
     * Get's the current logged in {@link User}.
     *
     * @return the {@link User}
     */
    @GetMapping("/me")
    @ResponseBody
    public User getLoggedInUser(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new EntityNotFoundException(User.class);
        return user;
    }
}