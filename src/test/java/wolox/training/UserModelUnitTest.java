package wolox.training;

import static org.assertj.core.api.Assertions.assertThat;
import static wolox.training.factories.BookFactory.getDefaultBook;
import static wolox.training.factories.UserFactory.getUserTroy;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotOwnedException;
import wolox.training.models.Book;
import wolox.training.models.User;

public class UserModelUnitTest {

    @Test
    public void whenTryingToSetEmptyName_thenItFails() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User(null, "someUsername", LocalDate.now(), "123");
        });
        assertThat(exception.getMessage()).isEqualTo("name cannot be null!");
    }

    @Test
    public void whenTryingToBuildWithoutUsername_thenItFails() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User("SomeName", null, LocalDate.now(), "123");
        });
        assertThat(exception.getMessage()).isEqualTo("username cannot be null!");
    }

    @Test
    public void whenTryingToBuildWithEmptyStringAsName_thenItFails() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User("", "someUsername", LocalDate.now(), "123");
        });
        assertThat(exception.getMessage()).isEqualTo("name cannot be empty!");
    }

    @Test
    public void whenTryingToBuildWithNullBirthDate_thenItFails() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new User("someName", "someUsername", null, "123");
        });
        assertThat(exception.getMessage()).isEqualTo("birthDate cannot be null!");
    }

    @Test
    public void whenTryingToAssignNotOwnedBook_thenItIsAssigned(){
        Book b = getDefaultBook("some little book");
        User u = getUserTroy();
        u.assignBook(b);
        assertThat(u.getBooks().stream().findFirst().get().getTitle()).isEqualTo(b.getTitle());
    }

    @Test
    public void whenTryingToDeassignOwnedBook_thenItIsDeassigned(){
        Book b = getDefaultBook("some little book");
        User u = getUserTroy();
        u.assignBook(b);
        assertThat(u.getBooks().stream().findFirst().get().getTitle()).isEqualTo(b.getTitle());
        u.deassignBook(b);
        assertThat(u.getBooks().stream().findFirst().orElse(null)).isEqualTo(null);
    }



    @Test
    public void whenTryingToDeassignNotOwnedBook_thenItFails(){
        Book b = getDefaultBook("some little book");
        User u = getUserTroy();
        Exception exception = Assertions.assertThrows(BookNotOwnedException.class, () -> {
            u.deassignBook(b);
        });
        assertThat(exception.getMessage())
            .isEqualTo(String.format("User %d does not own book %d", u.getId(), b.getId()));
    }

    @Test
    public void whenTryingToAssignAlreadyOwnedBook_thenItFails(){
        Book b = getDefaultBook("some little book");
        User u = getUserTroy();
        u.assignBook(b);
        Exception exception = Assertions.assertThrows(BookAlreadyOwnedException.class, () -> {
            u.assignBook(b);
        });
        assertThat(exception.getMessage())
            .isEqualTo(String.format("User %d already owns book %d", u.getId(), b.getId()));    }

}