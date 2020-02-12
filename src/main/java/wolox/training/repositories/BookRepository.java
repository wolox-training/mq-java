package wolox.training.repositories;

import org.springframework.data.repository.CrudRepository;
import wolox.training.models.Book;

public interface BookRepository extends CrudRepository<Book, Long> {
    Book findByTitle(String title);
}
