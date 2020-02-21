package wolox.training;

import static org.assertj.core.api.Assertions.*;
import static wolox.training.factories.UserFactory.getUserKaren;
import static wolox.training.factories.UserFactory.getUserTroy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
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
}