package wolox.training.exceptions;

import wolox.training.models.Book;
import wolox.training.models.User;

public class BookNotOwnedException extends RuntimeException {
    public BookNotOwnedException(User user, Book book){
        super(String.format("User %s does not own book %s", user.getId(), book.getId()));
    }
}
