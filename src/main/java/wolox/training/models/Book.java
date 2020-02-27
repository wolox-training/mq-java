package wolox.training.models;

import static com.google.common.base.Preconditions.checkArgument;
import static wolox.training.utils.PropertyValidationUtils.checkString;

import io.swagger.annotations.ApiModel;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Book is the main entity we'll be using for the java training
 *
 * @author tinoq-woloxer
 */
@Entity
@Data
@ApiModel(description = "Model of a book that may be assigned to users")
@EqualsAndHashCode
@NoArgsConstructor
public class Book {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String genre;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subtitle;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String year;

    @Column(nullable = false)
    private int pages;

    @Column(nullable = false)
    private String isbn;

    public Book(
        String title,
        String author,
        String image,
        String subtitle,
        String publisher,
        String year,
        int pages,
        String isbn
    ){
        setTitle(title);
        setAuthor(author);
        setImage(image);
        setSubtitle(subtitle);
        setPublisher(publisher);
        setYear(year);
        setPages(pages);
        setIsbn(isbn);
    }

    public void setAuthor(String author){
        this.author = checkString(author, "author");
    }

    public void setImage(String image){
        this.image = checkString(image, "image");
    }

    public void setTitle(String title){
        this.title = checkString(title, "title");
    }

    public void setSubtitle(String subtitle){
        this.subtitle = checkString(subtitle, "subtitle");
    }

    public void setPublisher(String publisher){
        this.publisher = checkString(publisher, "publisher");
    }

    public void setYear(String year){
        this.year = checkString(year, "year");
    }

    public void setPages(int pages){
        checkArgument(
            pages >= 0,
            "Pages must be greater or equal than zero" );
        this.pages = pages;
    }

    public void setIsbn(String isbn){
        this.isbn = checkString(isbn, "isbn");
    }
}
