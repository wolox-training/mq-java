package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import wolox.training.models.Book;
import wolox.training.models.User;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Book Not Owned")
public class BookNotOwnedException extends RuntimeException {
    public BookNotOwnedException(User user, Book book){
        super(String.format("User %s does not own book %s", user.getId(), book.getId()));
    }
}
