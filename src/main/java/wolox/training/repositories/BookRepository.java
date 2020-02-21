package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import wolox.training.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(@NotNull String title);

    Optional<Book> findByIsbn(String isbn);
}
