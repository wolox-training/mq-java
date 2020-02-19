package wolox.training;

import static org.assertj.core.api.Assertions.assertThat;
import static wolox.training.factories.BookFactory.getDefaultBook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wolox.training.models.Book;

public class BookModelUnitTest {
    @Test
    public void whenTryingToSetEmptyTitle_thenItFails() {
        Book book = getDefaultBook("Nice little book");
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            book.setTitle("");
        });
        assertThat(exception.getMessage()).isEqualTo("title cannot be empty!");
    }

    @Test
    public void whenTryingToSetNullIsb_thenItFails() {
        Book book = getDefaultBook("Nice little book");
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            book.setIsbn(null);
        });
        assertThat(exception.getMessage()).isEqualTo("isbn cannot be null!");
    }
}