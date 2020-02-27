package wolox.training;

import static org.assertj.core.api.Assertions.assertThat;
import static wolox.training.factories.BookFactory.getDefaultBook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wolox.training.models.Book;

public class BookModelUnitTest {
    @Test
    public void whenTryingToSetEmptyTitle_thenItFails() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Book(
                "",
                "author",
                "image",
                "subtitle",
                "publisher",
                "year",
                1,
                "isbn"
            );
        });
        assertThat(exception.getMessage()).isEqualTo("title cannot be empty!");
    }

    @Test
    public void whenTryingToSetNullIsbn_thenItFails() {
        Book book = getDefaultBook("Nice little book");
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Book(
                "title",
                "author",
                "image",
                "subtitle",
                "publisher",
                "year",
                1,
                null
            );
        });
        assertThat(exception.getMessage()).isEqualTo("isbn cannot be null!");
    }
}