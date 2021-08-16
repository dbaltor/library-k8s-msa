package library.adapter.gateway;

import library.adapter.gateway.config.Propagate4xxFeignErrorDecoder.Feign4xxResponseException;
import library.adapter.gateway.port.ReaderClient;
import library.dto.Reader;
import library.usecase.port.ReaderService;

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

    @CircuitBreaker(name = "library-reader-service", fallbackMethod = "getReadersFallback")
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

    public List<Reader> loadDatabase(Optional<Integer> nReaders) { 
        return readerClient.loadDatabase(nReaders);
    }

    public void cleanUpDatabase() {
        readerClient.cleanUp();
    }
}
