package wolox.training.repositories;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import wolox.training.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(@NotNull String username);

    List<User> findByBirthDateBetweenAndNameContainingIgnoreCase(LocalDate start, LocalDate end, String contains);
}
