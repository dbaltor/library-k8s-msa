package reader.usecase;

import reader.dto.Reader;
import reader.usecase.port.ReaderRepository;
import reader.usecase.port.BookService;
import reader.usecase.port.ReaderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RefreshScope
@Service
@RequiredArgsConstructor @Slf4j
public class ReaderServiceImpl implements ReaderService {
    @Value("${reader.default-page-size:20}")
    private int DEFAULT_PAGE_SIZE;
    @Value("${reader.default-load-size:10}")
    private int DEFAULT_LOAD_SIZE;

    private final Faker faker = new Faker();
    
    private final @NonNull ReaderRepository readerRepository;
    private final @NonNull BookService bookService;

    /**
     * Load the Reader datastore generating ficticious readers
     * @param nReaders  the number of readers to be generated
     * @return          the list of generated readers
     * @see             ReaderService.cleanUpDatabase
     */
    public List<Reader> loadDatabase(Optional<Integer> nReaders) {
        val total = nReaders.orElse(DEFAULT_LOAD_SIZE);
        val readers = new ArrayList<Reader>(total);
        for(int i = 0; i < total; i++) {
            readers.add(
                Reader.builder()
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .dob(faker.date().birthday())
                    .address(faker.address().streetAddress())
                    .phone(faker.phoneNumber().phoneNumber())
                    .build()
            );
        }
        readerRepository.saveAll(readers);
        log.info(String.format("Reader database loaded with %d records", total));
        return readers;
    }

    /**
     * Clean up the Reader datastore
     * @see ReaderService.loadDatabase
     */
    public void cleanUpDatabase() {
        readerRepository.deleteAll();
        log.info("Reader database cleaned up...");        
    }

    /**
     * Retrieve the list of readers currently stored in the Reader datastore
     * @param pageNum   the number of the page to be retrieved.
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied
     * @return          the list of readers retrieved
     */
    public List<Reader> retrieveReaders(Optional<Integer> pageNum, Optional<Integer> pageSize) {
        val readers = new ArrayList<Reader>();
        if (pageNum.isPresent() ) {
            readerRepository
                .findAll(pageNum.get(), pageSize.orElse(DEFAULT_PAGE_SIZE))
                .forEach(reader -> {
                    reader.getBooks().addAll(bookService.findBooksByReaderId(reader.getId()));
                    readers.add(reader);
                });
        }
        else {
            readerRepository
                .findAll()
                .forEach(reader -> {
                    reader.getBooks().addAll(bookService.findBooksByReaderId(reader.getId()));
                    readers.add(reader);
                });
        }
        return readers;
    }
   
    /**
     * Retrieve a reader by their id, if they exist.
     * @param id    the id of the reader
     * @return      the reader found, if any
     */
    public Optional<Reader> retrieveReader(long id) {
        val reader = readerRepository.findById(id);
        if (reader.isPresent()) {
            reader.get().getBooks().addAll(
                bookService.findBooksByReaderId(
                    reader.get().getId()
                )
            );
        }
        return reader;
    }
}