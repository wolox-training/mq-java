package wolox.training.models;

import static wolox.training.utils.PropertyValidationUtils.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotOwnedException;
import wolox.training.utils.LocalDateSerializer;

@Entity
@Data
@Table(name = "users")
@ApiModel(description = "Model of a user that may own books")
@EqualsAndHashCode
@NoArgsConstructor
public class User {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDate;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "users_books",
        joinColumns = @JoinColumn(name = "users_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id"))
    @Setter(AccessLevel.PRIVATE)
    private Set<Book> books = new HashSet<Book>();

    public User(String name, String username, LocalDate birthDate){
        setUsername(username);
        setName(name);
        setBirthDate(birthDate);
    }

    private void setUsername(String username){
        this.username = checkString(username, "username");
    }

    private void setName(String name){
        this.name = checkString(name, "name");
    }

    private void setBirthDate(LocalDate birthDate){
        this.birthDate = checkLocalDate(birthDate, "birthDate");
    }

    public Set<Book> getBooks() {
        return (Set<Book>) Collections.unmodifiableSet(books);
    }

    public void assignBook(Book book) {
        if (!books.add(book))
            throw new BookAlreadyOwnedException(this, book);
    }

    public void deassignBook(Book book) {
        if (!books.remove(book))
            throw new BookNotOwnedException(this, book);
    }
}
