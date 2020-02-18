package wolox.training.exceptions;

import wolox.training.models.User;
import wolox.training.models.Book;

public class BookAlreadyOwnedException extends RuntimeException {
    public BookAlreadyOwnedException(User user, Book book){
        super(String.format("User %s already owns book %s", user.getId(), book.getId()));
    }
}
