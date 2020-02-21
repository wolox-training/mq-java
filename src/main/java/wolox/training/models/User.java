package wolox.training.models;

import static wolox.training.utils.Utils.*;

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
import lombok.Setter;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotOwnedException;
import wolox.training.utils.LocalDateSerializer;

@Entity
@Data
@Table(name = "users")
@ApiModel(description = "Model of a user that may own books")
@EqualsAndHashCode
public class User {
    private User(){}

    public User(String name, String username, LocalDate birthDate, String encodedPassword) {
        setUsername(username);
        setName(name);
        setBirthDate(birthDate);
        setPassword(encodedPassword);
    }

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String password;

    public void setPassword(String password){
        this.password = checkString(password, "password");
    }

    @Column(nullable = false)
    private String role = "USER";
    public void setRole(String role){
        this.role = checkString(role, "role");
    }

    @Column(nullable = false)
    private String username;
    public void setUsername(String username){
        this.username = checkString(username, "username");
    }

    @Column(nullable = false)
    private String name;
    public void setName(String name){
        this.name = checkString(name, "name");
    }

    @Column(nullable = false)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDate;
    public void setBirthDate(LocalDate birthDate){
        this.birthDate = checkLocalDate(birthDate, "birthDate");
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "users_books",
        joinColumns = @JoinColumn(name = "users_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id"))
    @Setter(AccessLevel.PRIVATE)
    private Set<Book> books = new HashSet<Book>();

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
