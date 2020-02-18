package wolox.training.models;

import static wolox.training.utils.Utils.*;

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
import lombok.Setter;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotOwnedException;

@Entity
@Data
@Table(name = "users")
@ApiModel(description = "Model of a user that may own books")
public class User {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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
    private LocalDate birthDate;
    public void setBirthDate(LocalDate birthDate){
        this.birthDate = checkLocalDate(birthDate, "birthDate");
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "users_books",
        joinColumns = @JoinColumn(name = "users_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id"))
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
