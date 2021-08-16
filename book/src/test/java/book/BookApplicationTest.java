package book;

import book.usecase.port.BookService;
import book.adapter.gateway.ReaderEntityGateway;
import book.dto.Book;
import book.usecase.port.BookRepository;
import book.usecase.exception.BorrowingException;
import book.usecase.exception.ReturningException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

import com.github.javafaker.Faker;
import lombok.val;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = BookApplication.class,
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureStubRunner(
		ids = "cf.dbaltor:reader:1.0.0:stubs:8002",
		stubsMode = StubRunnerProperties.StubsMode.LOCAL,
		consumerName = "bookConsumer"
)
public class BookApplicationTest{
	@Autowired private BookService bookService;
	@Autowired private BookRepository bookRepository;

    @MockBean ReaderEntityGateway readerEntityGateway; // skip remote calls

	private final Faker faker = new Faker();
	private static final int NUM_TEST_BOOKS = 20;
	
    @After
    public void teardown() {
		// Delete all books
		bookService.cleanUpDatabase();
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void shouldFindById() {
			//Given
			var books = List.of(
					Book.builder().name("Java").author("").genre("").publisher("").build(),
					Book.builder().name("Go").author("").genre("").publisher("").build(),
					Book.builder().name("Node").author("").genre("").publisher("").build());
			books = bookRepository.saveAll(books);
			//When
			val book = bookService.retrieveBook(books.get(0).getId());
			//Then
			assertTrue(book.isPresent());
	}

	@Test
	public void shouldFindByIds() {
			//Given
			val ids = Stream.iterate(0, e -> e + 1)
							.limit(NUM_TEST_BOOKS)
							.map(e -> Book.builder()
											.name(faker.book().title())
											.author(faker.book().author())
											.genre(faker.book().genre())
											.publisher(faker.book().publisher())
											.build())
							.map(book -> bookRepository.save(book))
							.map(book -> book.getId())
							.collect(toList());
			//When
			val books = bookService.findBooksByIds(ids);
			//Then
			assertThat(books.size(), is(NUM_TEST_BOOKS));
	}

	@Test
	public void shouldFindByReaderId() throws BorrowingException {
		//Given
		var books = List.of(
			Book.builder().name("Java").author("").genre("").publisher("").build(),
			Book.builder().name("Go").author("").genre("").publisher("").build(),
			Book.builder().name("Node").author("").genre("").publisher("").build());
		books = bookRepository.saveAll(books);
		bookService.borrowBooks(books, 1L);
		//When
		val borrowedBooks = bookService.findBooksByReaderId(1L);
		//Then
		assertThat(borrowedBooks.size(), is(3));
	}	

	@Test
	public void shouldRetrieveAllBooks() {
			//Given
			bookService.loadDatabase(Optional.of(NUM_TEST_BOOKS), List.of());
			//When
			val books = bookService.retrieveBooks(Optional.empty(), Optional.empty());
			//Then
			assertThat(books.size(), is(NUM_TEST_BOOKS));
	}

	@Test
	public void shouldBorrowBooks() throws BorrowingException{
			// Given
			var books = List.of(
					Book.builder().name("Java").author("").genre("").publisher("").build(),
					Book.builder().name("Go").author("").genre("").publisher("").build(),
					Book.builder().name("Node").author("").genre("").publisher("").build());
			books = bookRepository.saveAll(books);
			// When
			bookService.borrowBooks(books, 1L);
			// Then
			val borrowedBooks = bookService.findBooksByReaderId(1L);
			assertThat(borrowedBooks.size(), is(books.size()));
	}

	@Test
	public void shouldReturnBooks() throws BorrowingException, ReturningException{
			// Given
			var books = List.of(
					Book.builder().name("Java").author("").genre("").publisher("").build(),
					Book.builder().name("Go").author("").genre("").publisher("").build(),
					Book.builder().name("Node").author("").genre("").publisher("").build());
			books = bookRepository.saveAll(books);
			bookService.borrowBooks(books, 1L);
			// When
			bookService.returnBooks(books, 1L);
			// Then
			val borrowedBooks = bookService.findBooksByReaderId(1L);
			assertThat(borrowedBooks.size(), is(0));
	}
}
