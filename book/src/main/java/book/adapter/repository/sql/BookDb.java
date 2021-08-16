package book.adapter.repository.sql;

import book.dto.Book;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

@ToString @Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class BookDb {
    //@GeneratedValue(strategy = GenerationType.AUTO) // default approach equivalent to the below   
    private @Setter @Id long id;
    private @NonNull String name;
    private @NonNull String author;
    private @NonNull String genre;
    private @NonNull String publisher;
    // DDD aggregate id
    private @NonNull Long readerId;

    public Book book() {
        return Book.builder()
            .id(id)
            .name(name)
            .author(author) 
            .genre(genre)
            .publisher(publisher)
            .readerId(readerId)
            .build();
    }

    public static BookDb of(Book b) {
        val bookDb = BookDb.of(
            b.getName(),
            b.getAuthor(),
            b.getGenre(),
            b.getPublisher(),
            b.getReaderId());
        bookDb.setId(b.getId());
        return bookDb;
    }
}