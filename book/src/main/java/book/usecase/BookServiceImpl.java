package book.usecase;

import book.dto.Book;
import book.dto.Reader;
import book.usecase.port.BookRepository;
import book.usecase.port.BookService;
import book.usecase.exception.BorrowingException;
import book.usecase.exception.ReturningException;
import book.domain.port.ReaderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

import com.github.javafaker.Faker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@RefreshScope
@Service
@Slf4j @RequiredArgsConstructor 
public class BookServiceImpl implements BookService {
    @Value("${book.default-page-size:20}")
    private int DEFAULT_PAGE_SIZE;
    @Value("${book.default-load-size:100}")
    private int DEFAULT_LOAD_SIZE;
    @Value("${book.default-borrowed-books:40}")
    private int DEFAULT_BORROWED_BOOKS;

    private final @NonNull BookRepository bookRepository;
    private final @NonNull ReaderEntity readerEntity;

    private final Faker faker = new Faker();

    /**
     * Load the Book datastore generating ficticious exemplars of books.
     * @param nBooks    the number of books to be generated
     * @param readers   the list of readers to borrow the generated books across
     * @return          the list of generated books
     * @see             BookService.cleanUpDatabase   
     */
    public List<Book> loadDatabase(Optional<Integer> nBooks, @NonNull List<Reader> readers) { 
        val NON_READERS = 3;
        val hasReaders = readers.size() > 0;
        var nReaders = 0;
        if (hasReaders) {
             // Radndomly remove one reader who will not have any book assigned
            nReaders = readers.size();
            for (int i = 0; i < NON_READERS; i++){
                readers.remove((int)(Math.random() * nReaders));
                nReaders = readers.size();
            }
        }
        val total = nBooks.orElse(DEFAULT_LOAD_SIZE);
        val books = new ArrayList<Book>(total);   
        for(int i = 0; i < total; i++) {
            var book = Book.builder()
            .name(faker.book().title())
            .author(faker.book().author())
            .genre(faker.book().genre())
            .publisher(faker.book().publisher())
            .build();
            // Try to borrow each book to a differnt reader through all readers
            // or at least for DEFAULT_BORROWED_BOOKS books
            if (hasReaders && (i < nReaders || i < DEFAULT_BORROWED_BOOKS)) {
                try {
                    val reader = readers.get(i % nReaders);
                    borrowBooks(
                        List.of(book), 
                        reader.getId()
                    );
                    // Add book to the reader's borrowed books
                    reader.getBooks().add(book);
                } catch(BorrowingException e) {
                    // book could not be borrowed but need to be saved
                    book = bookRepository.save(book);
                }
            }
            else { // save book without being borrowed
                book = bookRepository.save(book);
            }
            books.add(book);
        }
        log.info(String.format("Book database loaded with %d records.", total));
        return books;
    }

    /**
     * Clean up the Book datastore
     * @see     BookService.loadDatabase
     */
    public void cleanUpDatabase() {
        bookRepository.deleteAll();
        log.info("Book database cleaned up...");
    }

    /**
     * Retrieve a book by its id, if it exist.
     * @param id    the id of the book
     * @return      the book found, if any
     */
    public Optional<Book> retrieveBook(long id) {
        return bookRepository.findById(id);
    }

    /**
     * Retrieve the list of books currently stored in the Book datastore.
     * @param pageNum   the number of the page to be retrieved. 
     *                  No pagination is to apply when this is not present
     * @param pageSize  the number of items per page when pagination is being applied
     * @return          list of books retrieved
     */
    public List<Book> retrieveBooks(Optional<Integer> pageNum, Optional<Integer> pageSize) {
        val books = new ArrayList<Book>();
        if (pageNum.isPresent()) {
            bookRepository
                .findAll(pageNum.get(), pageSize.orElse(DEFAULT_PAGE_SIZE))
                .forEach(books::add);
        }
        else {
            bookRepository
                .findAll()
                .forEach(books::add);
        }
        return books;
    }

    /**
     * Find books by their ids, if they exist.
     * @param ids   the ids of the books
     * @return      list of books found, if any.
     */
    public List<Book> findBooksByIds(List<Long> ids) {
         return bookRepository.findByIds(ids);
     }    

    /**
     * Find books borrowed by a reader.
     * @param id    reader id
     * @return      list of books borrowed by the reader
     */
    public List<Book> findBooksByReaderId(long id) {
        return bookRepository.findByReaderId(id);
    }

    /**
     * Try to borrow books to the reader applying business rules.
     * @param booksToBorrow The list of books to borrow
     * @param reader        The reader borrowing the books
     * @return              The list of books effectively borrowed
     * @throws BorrowingExcecption
     */
    public List<Book> borrowBooks(List<Book> booksToBorrow, Long readerId) throws BorrowingException {
        // filter out books already borrowed
        val books = booksToBorrow
            .stream()
            .filter(book -> book.getReaderId() == 0)
            .collect(toList());
        // Validate list as per business rules
        val errors = readerEntity.bookBorrowingValidator(
            readerId, 
            books,
            findBooksByReaderId(readerId));
        if (!errors.isEmpty()){
            throw new BorrowingException(errors);
        }
        var newBorrowedBooks = books.stream()
            .map(book -> {
                book.setReaderId(readerId); // associate the book to the reader
                return book;
                
            })
            .collect(toList());
        bookRepository.saveAll(newBorrowedBooks);
        return newBorrowedBooks; // return list of newly borrowed books
    }
   
    /**
     * Try to return books from the reader applying business rules.
     * @param booksToReturn The list of books being returned
     * @param reader        The reader returning the books
     * @return              The list of books effectively returned
     * @throws ReturningException
     */
    public List<Book> returnBooks(List<Book> booksToReturn, Long readerId) throws ReturningException {
        // filter out books not borrowed by this reader
        val books = booksToReturn
            .stream()
            .filter(book -> book.getReaderId() == readerId)
            .collect(toList());
        // Validate list as per business rules
        val errors = readerEntity.bookReturningValidator(
            readerId, 
            books,
            findBooksByReaderId(readerId));
        if (!errors.isEmpty()){
            throw new ReturningException(errors);
        }
        // Validate list as per business rules
        val returnedBooks = books.stream()
            .map(book -> {
                book.setReaderId(0); // disassociate the book from the reader
                return book;
            })
            .collect(toList());
        bookRepository.saveAll(returnedBooks);
        //reader.getBooks().removeAll(returnedBooks);
        return returnedBooks; // return list of returned books
    }
}