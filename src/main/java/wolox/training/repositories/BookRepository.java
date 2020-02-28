package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wolox.training.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(@NotNull String title);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByPublisherAndGenreAndYear(String publisher, String genre, String year);

    @Query("SELECT b FROM Book b WHERE "
        + "(:publisher IS NULL OR b.publisher = :publisher) AND "
        + "(:genre IS NULL OR b.genre = :genre) AND "
        + "(:year IS NULL OR b.year = :year)")
    List<Book> findByPublisherAndGenreAndYearCustom(String publisher, String genre, String year);
}
