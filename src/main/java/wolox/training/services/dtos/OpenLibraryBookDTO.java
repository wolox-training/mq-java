package wolox.training.services.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import wolox.training.models.Book;

@Data
@AllArgsConstructor
public class OpenLibraryBookDTO {
    private String isbn;
    private String title;
    private String subtitile;
    private List<String> publishers;
    private List<String> authors;
    private String publishedYear;
    private int pages;

    public Book getAsBook(){
        return new Book(
            getTitle(),
            String.join(", ", getAuthors()),
            "image",
            getSubtitile(),
            String.join(", ", getPublishers()),
            getPublishedYear(),
            getPages(),
            isbn
        );
    }
}