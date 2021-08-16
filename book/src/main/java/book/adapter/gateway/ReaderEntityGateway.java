package book.adapter.gateway;

import book.adapter.gateway.port.ReaderClient;
import book.adapter.gateway.port.ReaderClient.ValidationRequest;
import book.adapter.gateway.config.Propagate4xxFeignErrorDecoder.Feign4xxResponseException;
import book.domain.port.ReaderEntity;
import book.dto.Book;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RefreshScope
@Service
@RequiredArgsConstructor
public class ReaderEntityGateway implements ReaderEntity {
    @Value("${book.default-max-allowed-borrowed-books:2}")
    private int DEFAULT_MAX_ALLOWED_BORROWED_BOOKS;
    
    private final @NonNull ReaderClient readerClient;

    @CircuitBreaker(name = "library-reader-service", fallbackMethod = "validateBookBorrowingFallback")
    public Set<BorrowingErrors> bookBorrowingValidator(Long readerId, List<Book> booksToBorrow, List<Book> borrowedBooks) {
        return readerClient.validateBookBorrowing(
            readerId, 
            new ValidationRequest(booksToBorrow, borrowedBooks));
    }
    Set<BorrowingErrors> validateBookBorrowingFallback(
        Long readerId, 
        List<Book> booksToBorrow,
        List<Book> borrowedBooks,
        RuntimeException e) {
            if (e instanceof Feign4xxResponseException){ 
                throw e; // return 4xx error
            }
            if (booksToBorrow.size() + borrowedBooks.size() > DEFAULT_MAX_ALLOWED_BORROWED_BOOKS) { 
                return Set.of(BorrowingErrors.MAX_BORROWED_BOOKS_EXCEEDED);
            }
            return Set.of(); // allow borrowing
    }

    @CircuitBreaker(name = "library-reader-service", fallbackMethod = "validateBookReturningFallback")
    public Set<ReturningErrors> bookReturningValidator(Long readerId, List<Book> booksToReturn, List<Book> borrowedBooks) {
        return readerClient.validateBookReturning(
            readerId, 
            new ValidationRequest(booksToReturn, borrowedBooks));
    }
    Set<ReturningErrors> validateBookReturningFallback(
        Long readerId, 
        List<Book> booksToReturn,
        List<Book> borrowedBooks,
        RuntimeException e) {
            if (e instanceof Feign4xxResponseException){ 
                throw e; // return 4xx error
            }
            return Set.of(); // allow returning
    }
}

