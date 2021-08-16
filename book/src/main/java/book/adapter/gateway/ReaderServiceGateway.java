package book.adapter.gateway;

import book.adapter.gateway.config.Propagate4xxFeignErrorDecoder.Feign4xxResponseException;
import book.dto.Reader;
import book.usecase.port.ReaderService;
import book.adapter.gateway.port.ReaderClient;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReaderServiceGateway implements ReaderService {
    private final @NonNull ReaderClient readerClient;

    @CircuitBreaker(name = "library-book-service", fallbackMethod = "getReadersFallback")
    public List<Reader> retrieveReaders(
        Optional<Integer> pageNum,
        Optional<Integer> pageSize,
        Optional<Long> readerId) {
            return readerClient.getReaders(pageNum, pageSize, readerId); 
    }
    List<Reader> getReadersFallback(
        Optional<Integer> pageNum, 
        Optional<Integer> pageSize, 
        Optional<Long> readerId,
        RuntimeException e) {
            if (e instanceof Feign4xxResponseException){ 
                throw e; // return 4xx error
            }
            return List.of();
    }
}
