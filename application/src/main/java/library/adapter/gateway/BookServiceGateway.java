package library.adapter.gateway;

import library.adapter.gateway.config.Propagate4xxFeignErrorDecoder.Feign4xxResponseException;
import library.adapter.gateway.port.BookClient;
import library.adapter.gateway.port.BookClient.BooksRequest;
import library.dto.Book;
import library.usecase.port.BookService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceGateway implements BookService {
    private final @NonNull BookClient bookClient;

    @CircuitBreaker(name = "library-book-service", fallbackMethod = "getBooksFallback")
    public List<Book> retrieveBooks(
        Optional<Integer> pageNum, 
        Optional<Integer> pageSize, 
        Optional<Long> readerId) {
            return bookClient.getBooks(pageNum, pageSize, readerId);
    }
    List<Book> getBooksFallback(
        Optional<Integer> pageNum, 
        Optional<Integer> pageSize, 
        Optional<Long> readerId,
        RuntimeException e) {
            if (e instanceof Feign4xxResponseException){ 
                throw e; // return 4xx error
            }
            return List.of();
    }

    public List<Book> loadDatabase(Optional<Integer> nBooks){
        return bookClient.loadDatabase(nBooks);
    }

    public void cleanUpDatabase(){ 
        bookClient.cleanUp();
    }
    
    @CircuitBreaker(name = "library-book-service", fallbackMethod = "borrowBooksFallback")
    public List<Book> borrowBooks(BookServiceRequest booksRequest) {
        return bookClient.borrowBooks(
            BooksRequest.of(
                booksRequest.readerId, 
                booksRequest.bookIds));
    }
    List<Book> borrowBooksFallback(BookServiceRequest booksRequest, RuntimeException e){
        if (e instanceof Feign4xxResponseException){ 
            throw e; // return 4xx error
        }
        return List.of();
    }

    @CircuitBreaker(name = "library-book-service", fallbackMethod = "returnBooksFallback")
    public List<Book> returnBooks(BookServiceRequest booksRequest) {
        return bookClient.returnBooks(
            BooksRequest.of(
                booksRequest.readerId, 
                booksRequest.bookIds));
    }
    List<Book> returnBooksFallback(BookServiceRequest booksRequest, RuntimeException e) {
        if (e instanceof Feign4xxResponseException){ 
            throw e; // return 4xx error
        }
        return List.of();
    }
}
