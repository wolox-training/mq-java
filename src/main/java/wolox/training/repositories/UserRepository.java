package wolox.training.repositories;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wolox.training.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(@NotNull String username);

    List<User> findByBirthDateBetweenAndNameContainingIgnoreCase(LocalDate start, LocalDate end, String contains);

    @Query("SELECT u FROM User u WHERE "
        + "(:username IS NULL OR UPPER(u.username) = UPPER(:username)) AND "
        + "(cast(:start as date) is NULL OR u.birthDate >= :start) AND "
        + "(cast(:end as date) is NULL OR u.birthDate <= :end)")
    List<User> findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(String username, LocalDate start, LocalDate end);

    @Query("SELECT u FROM User u WHERE "
        + "(:name IS NULL OR u.name = :name) AND "
        + "(:username IS NULL OR u.username = :username) AND "
        + "(:role IS NULL OR u.role = :role) AND "
        + "(cast(:birthDate as date) is NULL OR u.birthDate <= :birthDate)")
    List<User> findAllCustom(
        String name,
        String username,
        String role,
        LocalDate birthDate
    );
}
