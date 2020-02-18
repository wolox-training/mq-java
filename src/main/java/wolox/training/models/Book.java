package wolox.training.models;

import static com.google.common.base.Preconditions.checkArgument;
import static wolox.training.utils.Utils.checkString;

import io.swagger.annotations.ApiModel;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Book is the main entity we'll be using for the java training
 *
 * @author tinoq-woloxer
 */
@Entity
@Data
@ApiModel(description = "Model of a book that may be assigned to users")
public class Book {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String genre;

    @Column(nullable = false)
    private String author;
    public void setAuthor(String author){
        this.author = checkString(author, "author");
    }

    @Column(nullable = false)
    private String image;
    public void setImage(String image){
        this.image = checkString(image, "image");
    }

    @Column(nullable = false)
    private String title;
    public void setTitle(String title){
        this.title = checkString(title, "title");
    }

    @Column(nullable = false)
    private String subtitle;
    public void setSubtitle(String subtitle){
        this.subtitle = checkString(subtitle, "subtitle");
    }

    @Column(nullable = false)
    private String publisher;
    public void setPublisher(String publisher){
        this.publisher = checkString(publisher, "publisher");
    }

    @Column(nullable = false)
    private String year;
    public void setYear(String year){
        this.year = checkString(year, "year");
    }

    @Column(nullable = false)
    private int pages;
    public void setPages(int pages){
        checkArgument(
            pages >= 0,
            "Pages must be greater or equal than zero" );
        this.pages = pages;
    }

    @Column(nullable = false)
    private String isbn;
    public void setIsbn(String isbn){
        this.isbn = checkString(isbn, "isbn");
    }
}
