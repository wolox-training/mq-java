package wolox.training.services.dtos;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

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
}
