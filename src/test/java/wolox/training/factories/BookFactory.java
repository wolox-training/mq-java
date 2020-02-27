package wolox.training.factories;

import wolox.training.models.Book;

public class BookFactory {
    public static Book getDefaultBook(String title){
        return new Book(title,
            "DefaultAuthor",
            "DefaultImage",
            "DefaultSubtitle",
            "DefaultPublisher",
            "1994",
            1,
            "Default isbn"
        );
    };

}
