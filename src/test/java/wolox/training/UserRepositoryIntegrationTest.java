package wolox.training;

import static org.assertj.core.api.Assertions.assertThat;
import static wolox.training.factories.UserFactory.getUserKaren;
import static wolox.training.factories.UserFactory.getUserTroy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import wolox.training.exceptions.EntityNotFoundException;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByName_thenReturnUser() {
        User troy = getUserTroy();

        entityManager.persist(troy);
        entityManager.flush();

        User found = userRepository.findByUsername(troy.getUsername());
        assertThat(found.getName()).isEqualTo(troy.getName());
        assertThat(found.getUsername()).isEqualTo(troy.getUsername());
        assertThat(found.getBirthDate()).isEqualTo(troy.getBirthDate());
    }

    @Test
    public void whenFindAll_thenReturnsAllUsers() {
        User troy = getUserTroy();
        User karen = getUserKaren();

        entityManager.persist(troy);
        entityManager.persist(karen);
        entityManager.flush();

        List<User> found = userRepository.findAll();
        User supposedlyKaren = found.stream()
            .filter(u -> u.getName().equals(karen.getName()))
            .findFirst().orElse(null);
        User supposedlyTroy = found.stream()
            .filter(u -> u.getName().equals(troy.getName()))
            .findFirst().orElse(null);
        assertThat(karen.getUsername()).isNotEqualTo(null);
        assertThat(troy.getUsername()).isNotEqualTo(null);
    }

    @Test
    public void whenDeleteUser_thenDoesNotReturnUser() {
        User troy = getUserTroy();

        entityManager.persist(troy);
        entityManager.flush();

        User dbTroy = userRepository.findByUsername(troy.getUsername());
        assertThat(dbTroy.getName()).isEqualTo(troy.getName());
        userRepository.delete(troy);
        assertThat(userRepository.findById(dbTroy.getId())).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteUserById_thenDoesNotReturnUser() {
        User troy = getUserTroy();

        entityManager.persist(troy);
        entityManager.flush();

        User dbTroy = userRepository.findByUsername(troy.getUsername());
        assertThat(dbTroy.getName()).isEqualTo(troy.getName());
        userRepository.deleteById(troy.getId());
        assertThat(userRepository.findById(dbTroy.getId())).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSearchingByBirthDateBetweenAndNameContainingIgnoreCase_thenReturnsUser() {
        User troy = getUserTroy();
        User karen = getUserKaren();

        entityManager.persist(troy);
        entityManager.persist(karen);
        entityManager.flush();

        User dbTroy = userRepository.findByBirthDateBetweenAndNameContainingIgnoreCase(
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1),
            "ro"
        ).stream().findFirst().orElseThrow(() -> new EntityNotFoundException(User.class));

        assertThat(dbTroy.getName()).isEqualTo(troy.getName());
    }

    @Test
    public void whenSearchingByBirthDateBetweenAndNameContainingIgnoreCaseCustom_thenReturnsUser() {
        LocalDate now = LocalDate.now();
        User troy = new User(
            "Troy",
            "WonderfulTroy",
            now,
            "troy's password"
        );

        User karen = new User(
            "Karen",
            "SillyKaren",
            now.minusDays(10),
            "karen's password"
        );

        entityManager.persist(karen);
        entityManager.persist(troy);
        entityManager.flush();

        List<User> allUsers = new ArrayList<>(userRepository
            .findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(null, null, null));
        List<User> onlyTroy = new ArrayList<>(userRepository
            .findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(troy.getUsername(), null,
                null));
        List<User> onlyKaren = new ArrayList<>(userRepository
            .findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(karen.getUsername(), null,
                null));
        List<User> onlyKarenByDate = new ArrayList<>(userRepository
            .findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(
                null,
                LocalDate.now().minusDays(15),
                LocalDate.now().minusDays(5)
            ));
        List<User> onlyTroyByDate = new ArrayList<>(
            userRepository.findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(
                null,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5)
            ));

        List<User> bothByLowerDate = new ArrayList<>(
            userRepository.findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(
                null,
                LocalDate.now().minusDays(15),
                null
            ));

        List<User> bothByUpperDate = new ArrayList<>(
            userRepository.findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(
                null,
                null,
                LocalDate.now().plusDays(15)
            ));

        List<User> onlyKarenIgnoringCase = new ArrayList<>(
            userRepository.findByBirthDateBetweenAndNameContainingIgnoreCaseCustom(
                karen.getUsername().toUpperCase(),
                null,
                null
            ));

        assertThat(allUsers.size()).isEqualTo(2);
        assertThat(bothByLowerDate.size()).isEqualTo(2);
        assertThat(bothByUpperDate.size()).isEqualTo(2);
        assertThat(onlyKaren.size()).isEqualTo(1);
        assertThat(onlyKarenByDate.size()).isEqualTo(1);
        assertThat(onlyTroy.size()).isEqualTo(1);
        assertThat(onlyTroyByDate.size()).isEqualTo(1);
        assertThat(onlyKarenIgnoringCase.size()).isEqualTo(1);

        assertThat(onlyTroy.get(0).getUsername()).isEqualTo(troy.getUsername());
        assertThat(onlyTroyByDate.get(0).getUsername()).isEqualTo(troy.getUsername());
        assertThat(onlyKaren.get(0).getUsername()).isEqualTo(karen.getUsername());
        assertThat(onlyKarenByDate.get(0).getUsername()).isEqualTo(karen.getUsername());
        assertThat(onlyKarenIgnoringCase.get(0).getUsername()).isEqualTo(karen.getUsername());
        assertThat(onlyKarenIgnoringCase.get(0).getUsername()).isNotEqualTo(karen.getUsername().toUpperCase());
    }
}