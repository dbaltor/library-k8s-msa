package library;

import library.dto.Book;
import library.dto.Reader;
import library.adapter.controller.port.BookController;
import library.adapter.controller.port.ReaderController;
import library.adapter.gateway.BookServiceGateway;
import library.adapter.gateway.ReaderServiceGateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.hamcrest.Matchers.containsString;

import com.github.javafaker.Faker;

import java.util.List;
import java.util.Date;
import java.util.Optional;

import lombok.val;

@RunWith(SpringRunner.class)
@SpringBootTest(
	classes = LibraryApplication.class, 
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureStubRunner(
		ids = {"cf.dbaltor:book:1.0.0:stubs:8000", "cf.dbaltor:reader:1.0.0:stubs:8001"},
		stubsMode = StubRunnerProperties.StubsMode.LOCAL,
		consumerName = "libraryConsumer"
)
@DirtiesContext
public class LibraryApplicationTest{
	@Autowired private MockMvc mockMvc;

	@MockBean private BookServiceGateway bookServiceGateway; // skip remote calls
	@MockBean private ReaderServiceGateway readerServiceGateway; // skip remote calls

	private final Faker faker = new Faker();

	@Test
	public void contextLoads() {
	}

	@Test
	public void shouldLoadHomePage() throws Exception {
		mockMvc.perform(get("/"))
			.andDo(print())
			.andExpect(view().name("Index"));
	}
	@Test
	public void shouldLoadBookDatabase() throws Exception {
		//Given
		val oneBook = List.of(Book.builder()
			.name(faker.book().title())
			.author(faker.book().author())
			.genre(faker.book().genre())
			.publisher(faker.book().publisher())
			.build());
		when(bookServiceGateway.loadDatabase(eq(Optional.of(1))))
			.thenReturn(oneBook);

		//When
		mockMvc.perform(post("/loadbooks?count=1"))
		//Then
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Book database loaded with 1 records")));
	}

	@Test
	public void shouldLoadReaderDatabase() throws Exception {
		//Given
		val oneReader = List.of(Reader.builder()
			.firstName(faker.name().firstName())
			.lastName(faker.name().lastName())
			.dob(new Date())
			.address(faker.address().streetAddress())
			.phone(faker.phoneNumber().phoneNumber())
			.build());
		when(readerServiceGateway.loadDatabase(eq(Optional.of(1))))
			.thenReturn(oneReader);
		
		//When
		mockMvc.perform(post("/loadreaders?count=1"))
		//Then
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Reader database loaded with 1 records")));
	}

	@Test
	public void shouldCleanUpDatabases() throws Exception {
		//Given
		
		//When
		mockMvc.perform(post("/cleanup"))
		//Then
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("All data have been removed.")));
	}

	@Test
	public void shouldRetrieveBooks() throws Exception {
		//Given
		
		//When
		mockMvc.perform(get("/listbooks"))
		//Then
		.andDo(print())
		.andExpect(view().name(BookController.BOOKS_TEMPLATE));
	}

	@Test
	public void shouldRetrieveReaders() throws Exception {
		//Given
		
		//When
		mockMvc.perform(get("/listreaders"))
		//Then
		.andDo(print())
		.andExpect(view().name(ReaderController.READERS_TEMPLATE));
	}
}
