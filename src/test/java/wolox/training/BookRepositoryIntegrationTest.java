package wolox.training;

import static org.assertj.core.api.Assertions.assertThat;
import static wolox.training.factories.BookFactory.getDefaultBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BookRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void whenFindByName_thenReturnBook() {
        Book book = getDefaultBook("Nice little book");
        entityManager.persist(book);
        entityManager.flush();

        List<Book> found = bookRepository.findByTitle(book.getTitle());
        assertThat(found.get(0).getTitle()).isEqualTo(book.getTitle());
        assertThat(found.get(0).getAuthor()).isEqualTo(book.getAuthor());
        assertThat(found.get(0).getGenre()).isEqualTo(book.getGenre());
        assertThat(found.get(0).getImage()).isEqualTo(book.getImage());
        assertThat(found.get(0).getIsbn()).isEqualTo(book.getIsbn());
        assertThat(found.get(0).getPages()).isEqualTo(book.getPages());
        assertThat(found.get(0).getPublisher()).isEqualTo(book.getPublisher());
        assertThat(found.get(0).getSubtitle()).isEqualTo(book.getSubtitle());
        assertThat(found.get(0).getYear()).isEqualTo(book.getYear());
    }

    @Test
    public void whenFindAll_thenReturnsAllBooks() {
        Book book1 = getDefaultBook("Book1");
        Book book2 = getDefaultBook("Book2");

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        List<Book> found = bookRepository.findAll();
        Book supposedlyBook1 = found.stream()
            .filter(u -> u.getTitle().equals(book1.getTitle()))
            .findFirst().orElse(null);
        Book supposedlyBook2 = found.stream()
            .filter(u -> u.getTitle().equals(book2.getTitle()))
            .findFirst().orElse(null);
        assertThat(book1).isNotEqualTo(null);
        assertThat(book2).isNotEqualTo(null);
    }

    @Test
    public void whenDeleteBook_thenDoesNotReturnBook() {
        Book book = getDefaultBook("Some Book");
        entityManager.persist(book);
        entityManager.flush();

        Book dbBook = bookRepository.findByTitle(book.getTitle())
            .stream().findFirst().orElse(null);
        assertThat(dbBook).isNotNull();
        assertThat(dbBook.getIsbn()).isEqualTo(book.getIsbn());
        bookRepository.delete(book);
        assertThat(bookRepository.findById(book.getId())).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteBookById_thenDoesNotReturnBook() {
        Book book = getDefaultBook("Some Book");

        entityManager.persist(book);
        entityManager.flush();

        Book dbBook = bookRepository.findByTitle(book.getTitle())
            .stream().findFirst().orElse(null);
        assertThat(dbBook).isNotNull();
        assertThat(dbBook.getIsbn()).isEqualTo(book.getIsbn());
        bookRepository.deleteById(book.getId());
        assertThat(bookRepository.findById(book.getId())).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSearchingByPublisherGenreAndYear_thenReturnsMatchingBook() {
        Book book = getDefaultBook("Some Book");

        entityManager.persist(book);
        entityManager.flush();

        Optional<Book> dbBook = bookRepository.findByPublisherAndGenreAndYear(
            book.getPublisher(),
            book.getGenre(),
            book.getYear()
        ).stream().findFirst();
        assertThat(dbBook.isPresent()).isTrue();
        assertThat(dbBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    public void whenSearchingByPublisherGenreAndYearCustom_thenReturnsMatchingBook() {
        String greatestPublisher = "TheBestPublisher";
        Book coolBook = getDefaultBook("Some Cool Book");
        coolBook.setPublisher(greatestPublisher);
        coolBook.setYear("2020");

        Book boringBook = getDefaultBook("Some Boring Book");
        boringBook.setGenre("Drama");
        boringBook.setPublisher(greatestPublisher);

        entityManager.persist(coolBook);
        entityManager.persist(boringBook);
        entityManager.flush();

        List<Book> bothBooksByPublisher = new ArrayList<>(
            bookRepository.findByPublisherAndGenreAndYearCustom(
                greatestPublisher,
                null,
                null
            ));

        List<Book> onlyTheBoringBook = new ArrayList<>(
            bookRepository.findByPublisherAndGenreAndYearCustom(
                null,
                boringBook.getGenre(),
                null
            ));

        List<Book> onlyTheCoolBook = new ArrayList<>(
            bookRepository.findByPublisherAndGenreAndYearCustom(
                null,
                null,
                coolBook.getYear()
            ));

        assertThat(bothBooksByPublisher.size()).isEqualTo(2);
        assertThat(onlyTheBoringBook.size()).isEqualTo(1);
        assertThat(onlyTheCoolBook.size()).isEqualTo(1);

        assertThat(onlyTheCoolBook.get(0).getTitle()).isEqualTo(coolBook.getTitle());
        assertThat(onlyTheBoringBook.get(0).getTitle()).isEqualTo(boringBook.getTitle());
    }
}