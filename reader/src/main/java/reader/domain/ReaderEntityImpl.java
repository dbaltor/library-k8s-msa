package reader.domain;

import reader.dto.Book;
import reader.domain.port.ReaderEntity;
import reader.usecase.port.ReaderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.*;

import lombok.NonNull;
import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@RefreshScope
@Service
@RequiredArgsConstructor
public class ReaderEntityImpl implements ReaderEntity {
    @Value("${reader.max-allowed-borrowed-books:6}")
    private int MAXIMUM_ALLOWED_BORROWED_BOOKS;

    private final @NonNull ReaderService readerService;

    public Set<BorrowingErrors> bookBorrowingValidator(Long readerId, List<Book> booksToBorrow, List<Book> borrowedBooks) {
        // List of all validation criteria to be applied
        Map<BorrowingErrors, Predicate<List<Book>>> validators = new HashMap<>();
        val reader = readerService.retrieveReader(readerId);
       
        // Validation criterium: reader found
        Predicate<List<Book>> readerNotFound =
            books -> !reader.isPresent();
        validators.put(BorrowingErrors.READER_NOT_FOUND, readerNotFound);

        // Validation criterium: maximum borrowing books not exceeded
        Predicate<List<Book>> maxBorrowingExceeded = 
             books -> books.size() + borrowedBooks.size() > MAXIMUM_ALLOWED_BORROWED_BOOKS;
        validators.put(BorrowingErrors.MAX_BORROWED_BOOKS_EXCEEDED, maxBorrowingExceeded);
        
        // Future additional criteria
        // ...        

        // Strategy pattern combining all criteria
        return validators.entrySet()
            .stream()
            .filter(map -> map.getValue().test(booksToBorrow)) // filter out the successful ones
            .map(map -> map.getKey())
            .collect(toSet());


        /*=======================================================
        /*Predicate<List<Book>> combinedValidator = validators
            .stream()
            .reduce(v -> true, Predicate::and);
        return combinedValidator.test(booksToBorrow); 
        =======================================================*/
    }
   
    public Set<ReturningErrors> bookReturningValidator(Long readerId, List<Book> booksToReturn, List<Book> borrowedBooks) {
        // List of all validation criteria to be applied
        Map<ReturningErrors, Predicate<List<Book>>> validators = new HashMap<>();
        val reader = readerService.retrieveReader(readerId);
       
        // Validation criterium: reader found
        Predicate<List<Book>> readerNotFound =
            books -> !reader.isPresent();
        validators.put(ReturningErrors.READER_NOT_FOUND, readerNotFound);
        
        // Future additional criteria
        // ...        

        // Strategy pattern combining all criteria
        return validators.entrySet()
            .stream()
            .filter(map -> map.getValue().test(booksToReturn)) // filter out the successful ones
            .map(map -> map.getKey())
            .collect(toSet());
    }
}