package wolox.training.repositories;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import wolox.training.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(@NotNull String username);

    @Override
    <S extends User> S save(S entity);
}
