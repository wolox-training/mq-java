package wolox.training;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wolox.training.utils.PropertyValidationUtils.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wolox.training.controllers.BookController;
import wolox.training.controllers.UserController;
import wolox.training.factories.BookFactory;
import wolox.training.factories.UserFactory;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository mockUserRepository;

    @MockBean
    private BookController mockBookController;

    @Test
    public void whenPostingAUser_thenReturnsTheUser() throws Exception {
        User userToCreate = new User("ho", "asd", LocalDate.now());
        Mockito.when(mockUserRepository.save(userToCreate)).thenReturn(userToCreate);
        mockMvc.perform( MockMvcRequestBuilders
            .post("/api/users")
            .content(asJsonString(userToCreate))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(userToCreate.getName()))
            .andExpect(jsonPath("$.username").value(userToCreate.getUsername()))
            .andExpect(content().string(asJsonString(userToCreate)));
    }

    @Test
    public void whenGettingAllUsers_thenReturnsAllUsers() throws Exception {
        User troy = UserFactory.getUserTroy();
        User karen = UserFactory.getUserKaren();
        Mockito.when(mockUserRepository.findAll()).thenReturn(new ArrayList<User>(Arrays.asList(troy, karen)));
        mockMvc.perform( MockMvcRequestBuilders
            .get("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath(
                String.format("$[?(@.username == \'%s\')]", troy.getUsername())).exists()
            )
            .andExpect(jsonPath(
                String.format("$[?(@.username == \'%s\')]", karen.getUsername())).exists()
            );
    }

    @Test
    public void whenAssigningBook_thenRespondsNoContentAndAssignsBook() throws Exception {
        User troy = UserFactory.getUserTroy();
        Book book = BookFactory.getDefaultBook("some nice book");
        Mockito.when(mockUserRepository.findById(troy.getId())).thenReturn(Optional.of(troy));
        Mockito.when(mockBookController.findOne(book.getId())).thenReturn(book);
        Mockito.when(mockUserRepository.save(troy)).thenReturn(troy);

        mockMvc.perform( MockMvcRequestBuilders
            .post(String.format("/api/users/%d/assignBook/%d", troy.getId(), book.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertThat(troy.getBooks().stream().findFirst().get().getTitle())
            .isEqualTo(book.getTitle());
    }

    @Test
    public void whenDeassigningBook_thenRespondsNoContentAndDeassignsBook() throws Exception {
        User troy = UserFactory.getUserTroy();
        Book book = BookFactory.getDefaultBook("some nice book");
        troy.assignBook(book);

        Mockito.when(mockUserRepository.findById(troy.getId())).thenReturn(Optional.of(troy));
        Mockito.when(mockBookController.findOne(book.getId())).thenReturn(book);
        Mockito.when(mockUserRepository.save(troy)).thenReturn(troy);

        mockMvc.perform( MockMvcRequestBuilders
            .post(String.format("/api/users/%d/deassignBook/%d", troy.getId(), book.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertThat(troy.getBooks().stream().findFirst().orElse(null))
            .isEqualTo(null);
    }

    @Test
    public void whenRequestingUsersWithUsernameQueryParam_thenReturnsMatchingUsers() throws Exception {
        User troy = UserFactory.getUserTroy();
        User karen = UserFactory.getUserKaren();
        Mockito.when(mockUserRepository.findByUsername(troy.getUsername())).thenReturn(
            new ArrayList<User>(Arrays.asList(troy))
        );
        mockMvc.perform( MockMvcRequestBuilders
            .get("/api/users")
            .param("username", troy.getUsername())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                String.format("$[?(@.username == \'%s\')]", troy.getUsername())).exists()
            )
            .andExpect(jsonPath(
                String.format("$[?(@.username == \'%s\')]", karen.getUsername())).doesNotExist()
            );
    }
}
