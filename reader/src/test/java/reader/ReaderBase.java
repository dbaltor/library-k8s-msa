package reader;

import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;

import java.util.stream.Stream;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import static java.util.stream.Collectors.*;
import lombok.val;

import reader.adapter.controller.port.ReaderServiceController;
import reader.usecase.port.ReaderService;
import reader.dto.Reader;
import reader.domain.port.ReaderEntity;
import reader.domain.port.ReaderEntity.BorrowingErrors;
import reader.domain.port.ReaderEntity.ReturningErrors;

import com.github.javafaker.Faker;

@SpringBootTest(classes = ReaderApplication.class)
public abstract class ReaderBase {
    @Autowired ReaderServiceController readerServiceController;

    @MockBean ReaderService readerService;
    @MockBean ReaderEntity readerEntity;
    
    private static final int NUM_TEST_READERS = 10;
    private final Faker faker = new Faker();
 
    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(readerServiceController);
        
        // bug content type => https://github.com/spring-cloud/spring-cloud-contract/issues/1428
        EncoderConfig encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
		RestAssuredMockMvc.config = new RestAssuredMockMvcConfig().encoderConfig(encoderConfig);
            
        // Given
        val readers = Stream.iterate(0, e -> e + 1)
            .limit(NUM_TEST_READERS)
            .map(e -> Reader.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .dob(new Date())
                .address(faker.address().streetAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build())
            .collect(toList());
        val reader = Reader.builder()
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .dob(new Date())
            .address(faker.address().streetAddress())
            .phone(faker.phoneNumber().phoneNumber())
            .build();
        reader.setId(1L);

        when(readerService.loadDatabase(eq(Optional.of(1))))
            .thenReturn(List.of(reader));
        
        when(readerService.loadDatabase(eq(Optional.empty())))
            .thenReturn(readers);

        when(readerService.retrieveReaders(Optional.empty(), Optional.empty()))
            .thenReturn(readers);

        when(readerService.retrieveReader(eq(1L)))
            .thenReturn(Optional.of(reader));
            
        when(readerEntity.bookBorrowingValidator(eq(9999L), any(), any()))
            .thenReturn(Set.of(BorrowingErrors.READER_NOT_FOUND));

        when(readerEntity.bookReturningValidator(eq(9999L), any(), any()))
            .thenReturn(Set.of(ReturningErrors.READER_NOT_FOUND));;
    }
}
