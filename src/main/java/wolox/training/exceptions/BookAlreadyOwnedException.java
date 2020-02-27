package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import wolox.training.models.User;
import wolox.training.models.Book;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Book already owned")
public class BookAlreadyOwnedException extends RuntimeException {
    public BookAlreadyOwnedException(User user, Book book){
        super(String.format("User %s already owns book %s", user.getId(), book.getId()));
    }
}
