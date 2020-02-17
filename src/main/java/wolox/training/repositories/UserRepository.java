package wolox.training.repositories;

import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import wolox.training.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(@NotNull String username);
}
