package reader;

import reader.usecase.port.ReaderService;
import reader.adapter.gateway.BookServiceGateway;
import reader.dto.Reader;
import reader.usecase.port.ReaderRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.val;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReaderApplication.class)
@AutoConfigureMockMvc
public class ReaderApplicationTest{
	@Autowired private ReaderService readerService;
	@Autowired private ReaderRepository readerRepository;

	@MockBean private BookServiceGateway bookServiceGateway; // skip remote calls
	
	private static final int NUM_TEST_READERS = 5;

    @After
    public void teardown() {
		// Delete all readers
		readerService.cleanUpDatabase();
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void shouldFindById() {
			//Given
			var readers = List.of(
				Reader.builder().firstName("John1").lastName("Doe1").dob(new Date()).address("").phone("").build(),
				Reader.builder().firstName("John2").lastName("Doe2").dob(new Date()).address("").phone("").build(),
				Reader.builder().firstName("John3").lastName("Doe3").dob(new Date()).address("").phone("").build());

			// Added to testBooks to be removed durig the teardown
			readers = readerRepository.saveAll(readers);
			//When
			val reader = readerService.retrieveReader(readers.get(0).getId());
			//Then
			assertTrue(reader.isPresent());
	}


	@Test
	public void shouldRetrieveAllReaders() {
			//Given
			readerService.loadDatabase(Optional.of(NUM_TEST_READERS));
			//When
			val readers = readerService.retrieveReaders(Optional.empty(), Optional.empty());
			//Then
			assertThat(readers.size(), is(NUM_TEST_READERS));
	}
}
