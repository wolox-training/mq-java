package wolox.training;

import static org.assertj.core.api.Assertions.*;
import static wolox.training.factories.UserFactory.getUserKaren;
import static wolox.training.factories.UserFactory.getUserTroy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

        List<User> found = userRepository.findByUsername(troy.getUsername());
        assertThat(found.get(0).getName()).isEqualTo(troy.getName());
        assertThat(found.get(0).getUsername()).isEqualTo(troy.getUsername());
        assertThat(found.get(0).getBirthDate()).isEqualTo(troy.getBirthDate());
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
            .filter(u -> u.getName() == karen.getName())
            .findFirst().orElse(null);
        User supposedlyTroy = found.stream()
            .filter(u -> u.getName() == troy.getName())
            .findFirst().orElse(null);
        assertThat(karen.getUsername()).isNotEqualTo(null);
        assertThat(troy.getUsername()).isNotEqualTo(null);
    }

    @Test
    public void whenDeleteUser_thenDoesNotReturnUser() {
        User troy = getUserTroy();

        entityManager.persist(troy);
        entityManager.flush();

        User dbTroy = userRepository.findByUsername(troy.getUsername()).get(0);
        assertThat(dbTroy.getName()).isEqualTo(troy.getName());
        userRepository.delete(troy);
        assertThat(userRepository.findById(dbTroy.getId())).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteUserById_thenDoesNotReturnUser() {
        User troy = new User("Troy", "WonderfulTroy", LocalDate.now());

        entityManager.persist(troy);
        entityManager.flush();

        User dbTroy = userRepository.findByUsername(troy.getUsername()).get(0);
        assertThat(dbTroy.getName()).isEqualTo(troy.getName());
        userRepository.deleteById(troy.getId());
        assertThat(userRepository.findById(dbTroy.getId())).isEqualTo(Optional.empty());
    }
}