package book;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import java.util.stream.Stream;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.*;

import lombok.val;

import book.adapter.controller.port.BookServiceController;
import book.adapter.gateway.ReaderServiceGateway;
import book.usecase.port.BookService;
import book.dto.Book;
import book.usecase.exception.BorrowingException;
import book.usecase.exception.ReturningException;

import com.github.javafaker.Faker;

@SpringBootTest(classes = BookApplication.class)
public abstract class BookBase {
    @Autowired BookServiceController bookServiceController;

    @MockBean BookService bookService;
    @MockBean ReaderServiceGateway readerServiceGateway; // skip remote calls
    
    private static final int NUM_TEST_BOOKS = 100;
    private final Faker faker = new Faker();

    @BeforeEach
    public void setup() throws BorrowingException, ReturningException {
        RestAssuredMockMvc.standaloneSetup(bookServiceController);
            
        // Given
        val oneBook = List.of(Book.builder()
            .name(faker.book().title())
            .author(faker.book().author())
            .genre(faker.book().genre())
            .publisher(faker.book().publisher())
            .build());
        val books = Stream.iterate(0, e -> e + 1)
            .limit(NUM_TEST_BOOKS)
            .map(e -> Book.builder()
                .name(faker.book().title())
                .author(faker.book().author())
                .genre(faker.book().genre())
                .publisher(faker.book().publisher())
                .build())
            .collect(toList());
		val readerId1Books = List.of(
			Book.builder().name("Java").author("author1").genre("genre1").publisher("publisher1").build(),
			Book.builder().name("Go").author("author2").genre("genre2").publisher("publisher2").build(),
            Book.builder().name("Node").author("author3").genre("genre3").publisher("publisher3").build());
        readerId1Books.forEach(book -> book.setReaderId(1));
        val returnedBooks = List.of(
                Book.builder().name("Java").author("author1").genre("genre1").publisher("publisher1").build(),
                Book.builder().name("Go").author("author2").genre("genre2").publisher("publisher2").build(),
                Book.builder().name("Node").author("author3").genre("genre3").publisher("publisher3").build());

        when(bookService.loadDatabase(eq(Optional.of(1)), any()))
            .thenReturn(oneBook);

        when(bookService.loadDatabase(eq(Optional.empty()), any()))
            .thenReturn(books);

        when(bookService.retrieveBooks(eq(Optional.empty()), eq(Optional.empty())))
            .thenReturn(books);

        when(bookService.findBooksByReaderId(eq(1L)))
            .thenReturn(readerId1Books);
            
        when(bookService.borrowBooks(any(), eq(1L)))
            .thenReturn(readerId1Books);
        
        when(bookService.returnBooks(any(), eq(1L)))
            .thenReturn(returnedBooks);
    }
}
