package reader.adapter.gateway;

import reader.dto.Book;
import reader.adapter.gateway.port.BookClient;
import reader.adapter.gateway.config.Propagate4xxFeignErrorDecoder.Feign4xxResponseException;
import reader.usecase.port.BookService;

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
    public List<Book> findBooksByReaderId(long id) {

        return bookClient.getBooks(
            Optional.empty(),   
            Optional.empty(),   
            Optional.of(id));
    }
    List<Book> getBooksFallback(long id, RuntimeException e) {
        if (e instanceof Feign4xxResponseException){ 
            throw e; // return 4xx error
        }
        return List.of();
    }
}

