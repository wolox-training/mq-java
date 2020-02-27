package wolox.training;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wolox.training.utils.Utils.asJsonString;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import wolox.training.controllers.BookController;
import wolox.training.factories.BookFactory;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.services.OpenLibrary;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository mockBookRepository;

    @MockBean
    private RestTemplateBuilder testRestTemplate;

    @Autowired
    private OpenLibrary openLibrary;

    @WithMockUser()
    @Test
    public void whenPostingABook_thenReturnsTheBook() throws Exception {
        Book bookToCreate = BookFactory.getDefaultBook("some little nice book");
        Mockito.when(mockBookRepository.save(bookToCreate)).thenReturn(bookToCreate);
        mockMvc.perform( MockMvcRequestBuilders
            .post("/api/books")
            .content(asJsonString(bookToCreate))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(bookToCreate.getTitle()))
            .andExpect(jsonPath("$.isbn").value(bookToCreate.getIsbn()))
            .andExpect(content().string(asJsonString(bookToCreate)));
    }

    @WithMockUser()
    @Test
    public void whenGettingAllBooks_thenReturnsAllBooks() throws Exception {
        Book book1 = BookFactory.getDefaultBook("book1");
        Book book2 = BookFactory.getDefaultBook("book2");

        List<Book> books = new ArrayList<Book>(Arrays.asList(book1, book2));
        Page<Book> pagedResponse = new PageImpl(books);
        Mockito.when(mockBookRepository.findAllCustom(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            PageRequest.of(0, 20)
        )).thenReturn(pagedResponse);

        mockMvc.perform( MockMvcRequestBuilders
            .get("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath(
                String.format("$.content.[?(@.title == \'%s\')]", book1.getTitle())).exists()
            )
            .andExpect(jsonPath(
                String.format("$.content.[?(@.title == \'%s\')]", book2.getTitle())).exists()
            );
    }

    @WithMockUser()
    @Test
    public void whenRequestingExistingBookById_thenReturnsTheBook() throws Exception {
        Book book = BookFactory.getDefaultBook("some nice book");
        Mockito.when(mockBookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        mockMvc.perform( MockMvcRequestBuilders
            .get(String.format("/api/books/%d", book.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(book.getTitle()))
            .andExpect(jsonPath("$.isbn").value(book.getIsbn()))
            .andExpect(content().string(asJsonString(book)));
    }

    @WithMockUser()
    @Test
    public void whenRequestingBooksWithTitleQueryParam_thenReturnsMatchingBooks() throws Exception {
        Book coolBook = BookFactory.getDefaultBook("someCoolBook");
        Book boringBook = BookFactory.getDefaultBook("someBoringBook");
        Mockito.when(mockBookRepository.findAllCustom(
            coolBook.getTitle(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            PageRequest.of(0, 20)
        )).thenReturn(new PageImpl(new ArrayList<Book>(Arrays.asList(coolBook))));

        mockMvc.perform( MockMvcRequestBuilders
            .get("/api/books")
            .param("title", coolBook.getTitle())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                String.format("$.content.[?(@.title == \'%s\')]", coolBook.getTitle())).exists()
            )
            .andExpect(jsonPath(
                String.format("$.content.[?(@.title == \'%s\')]", boringBook.getTitle())).doesNotExist()
            );
    }

    @WithMockUser()
    @Test
    public void whenRequestingBookWithPagesQueryParam_thenFiltersByPages() throws Exception {
        Book coolBook = BookFactory.getDefaultBook("someCoolBook");
        Book boringBook = BookFactory.getDefaultBook("someBoringBook");
        boringBook.setPages(10000);
        Mockito.when(mockBookRepository.findAllCustom(
            null,
            null,
            null,
            null,
            null,
            null,
            boringBook.getPages(),
            null,
            null,
            PageRequest.of(0, 20)
        )).thenReturn(new PageImpl(new ArrayList<Book>(Arrays.asList(boringBook))));

        Mockito.when(mockBookRepository.findAllCustom(
            null,
            null,
            null,
            null,
            null,
            null,
            coolBook.getPages(),
            null,
            null,
            PageRequest.of(0, 20)
        )).thenReturn(new PageImpl(new ArrayList<Book>(Arrays.asList(coolBook))));

        mockMvc.perform( MockMvcRequestBuilders
            .get("/api/books")
            .param("pages", Integer.toString(boringBook.getPages()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                String.format("$.content.[?(@.title == \'%s\')]", boringBook.getTitle())).exists()
            )
            .andExpect(jsonPath(
                String.format("$[?(@.title == \'%s\')]", coolBook.getTitle())).doesNotExist()
            );
    }

    @WithMockUser()
    @Test
    public void whenSearchingBookByIsbnFallsBackToOpenLibrary_thenReturnsNewSavedBook() throws Exception {
        String theIsbn = "9780812984965";
        Mockito.when(mockBookRepository.findByIsbn(theIsbn)).thenReturn(Optional.empty());
        Book book = OpenLibrary.buildDTOFromJsonString(
            theIsbn,
            String.join("", Files.readAllLines(Paths.get("__files/ISBN_9780812984965.json")))
        ).getAsBook();
        Mockito.when(mockBookRepository.save(book)).thenReturn(book);
        mockMvc.perform( MockMvcRequestBuilders
            .get(String.format("/api/books/isbn/%s", theIsbn))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(book.getTitle()))
            .andExpect(jsonPath("$.isbn").value(book.getIsbn()));
    }
}
